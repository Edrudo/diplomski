#include <stdio.h>

#include "freeglut-3.2.2/include/GL/freeglut.h"
#include <fstream>

#ifdef _WIN32
#else
#include <unistd.h>
#endif
#include <iostream>
#include <cstdlib>
#include <cmath>
#include <chrono>
#include <thread>

using namespace std;
using std::this_thread::sleep_for;

std::string line;

int numberOfPolygons = 0, numberOfVertexes = 0;

float xMin = -1, xMax = -1, yMin = -1, yMax = -1, zMin = -1, zMax = -1;

struct Vertex {
    float x;
    float y;
    float z;
};

struct Polygon {
    int a;
    int b;
    int c;

    float A_d;
    float B_d;
    float C_d;

    float D_d;
};

Vertex center;

Vertex *vertexes;
Polygon *polygons;

Vertex test;

Vertex glediste;
Vertex ociste;

int numberOfControlPoints;
Vertex *controlPolygon;
Vertex bezierPoints[100];
int bezierIdx = 50;

// region utils

void printVertex(Vertex v) {
    printf("vertex: %f %f %f\n", v.x, v.y, v.z);
}

Vertex sub(Vertex a, Vertex b) {
    Vertex v;

    v.x = a.x - b.x;
    v.y = a.y - b.y;
    v.z = a.z - b.z;

    return v;
}

void mulMat(float mat1[][4], float mat2[][4], float rslt[4][4]) {
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            rslt[i][j] = 0;

            for (int k = 0; k < 4; k++) {
                rslt[i][j] += mat1[i][k] * mat2[k][j];
            }
        }
    }
}

void apply(float mat1[1][4], float mat2[4][4], float rslt[1][4]) {
    for (int i = 0; i < 1; i++) {
        for (int j = 0; j < 4; j++) {
            rslt[i][j] = 0;

            for (int k = 0; k < 4; k++) {
                rslt[i][j] += mat1[i][k] * mat2[k][j];
            }
        }
    }
}

void printMat(float mat[4][4]) {
    for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++) {
            cout << mat[i][j] << " ";
        }

        cout << endl;
    }
}

// endregion

void transformMatrix(float transformMat[4][4]) {
    printVertex(glediste);
    printVertex(ociste);

    float t1[4][4] = {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {-ociste.x, -ociste.y, -ociste.z, 1}
    };

    float xg1 = glediste.x - ociste.x;
    float yg1 = glediste.y - ociste.y;
    float zg1 = glediste.z - ociste.z;

    float sinAlpha = yg1 / sqrt(pow(xg1, 2) + pow(yg1, 2));
    float cosAlpha = xg1 / sqrt(pow(xg1, 2) + pow(yg1, 2));

    float t2[4][4] = {
            {cosAlpha, -sinAlpha, 0, 0},
            {sinAlpha, cosAlpha, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };

    float rslt[4][4];
    mulMat(t2, t2, rslt);

    float xg2 = sqrt(pow(xg1, 2) + pow(yg1, 2));
    float yg2 = 0;
    float zg2 = zg1;

    float sinBeta = xg2 / sqrt(pow(xg2, 2) + pow(zg2, 2));
    float cosBeta = zg2 / sqrt(pow(xg2, 2) + pow(zg2, 2));

    float t3[4][4] = {
            {cosBeta, 0, sinBeta, 0},
            {0, 1, 0, 0},
            {-sinBeta, 0, cosBeta, 0},
            {0, 0, 0, 1}
    };

    float t4[4][4] = {
            {0, -1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };

    float t5[4][4] = {
            {-1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };

    mulMat(t1, t2, transformMat);

    mulMat(transformMat, t3, rslt);
    transformMat = rslt;

    mulMat(transformMat, t4, rslt);
    transformMat = rslt;

    mulMat(transformMat, t5, rslt);
    transformMat = rslt;
}

float perspectiveMatrix(float perspectiveMat[4][4]) {
    float xg1 = glediste.x - ociste.x;
    float yg1 = glediste.y - ociste.y;
    float zg1 = glediste.z - ociste.z;

    float H = sqrt(pow(xg1, 2) + pow(yg1, 2) + pow(zg1, 2));

    cout << "H: " << H << endl;

    float rslt[4][4] = {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 0, 1.f/H},
            {0, 0, 0, 0}
    };

    float ones[4][4] = {
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1}
    };

    mulMat(rslt, ones, perspectiveMat);

    return H;
}

void count() {
    std::ifstream infile("polygon.obj");

    char type;
    float x, y, z;
    while (infile >> type >> x >> y >> z)
    {
        switch (type) {
            case 'f':
                numberOfPolygons++;
                break;
            case 'v':
                numberOfVertexes++;
                break;
        }
    }
}

void load() {
    std::ifstream infile("polygon.obj");

    char type;
    float x, y, z;

    int currVertex = 0, currPoly = 0;
    float xSum = 0, ySum = 0, zSum = 0;

    while (infile >> type >> x >> y >> z)
    {
        switch (type) {
            case 'f':
                Polygon p;
                p.a = x - 1;
                p.b = y - 1;
                p.c = z - 1;

                polygons[currPoly++] = p;

                break;
            case 'v':
                if (xMin == -1 || x < xMin) {
                    xMin = x;
                }

                if (xMax == -1 || x > xMax) {
                    xMax = x;
                }

                if (yMin == -1 || y < yMin) {
                    yMin = y;
                }

                if (yMax == -1 || y > yMax) {
                    yMax = y;
                }

                if (zMin == -1 || z < zMin) {
                    zMin = z;
                }

                if (zMax == -1 || z > zMax) {
                    zMax = z;
                }

                xSum += x;
                ySum += y;
                zSum += z;

                Vertex v;
                v.x = x;
                v.y = y;
                v.z = z;

                vertexes[currVertex++] = v;

                break;
        }
    }

    center.x = xSum / numberOfVertexes;
    center.y = ySum / numberOfVertexes;
    center.z = zSum / numberOfVertexes;
}

void scale() {
    for (int i = 0; i < numberOfVertexes; ++i) {
        // center
        vertexes[i].x = vertexes[i].x - center.x;
        vertexes[i].y = vertexes[i].y - center.y;
        vertexes[i].z = vertexes[i].z - center.z;
    }

    xMax = xMax - center.x;
    yMax = yMax - center.y;
    zMax = zMax - center.z;

    for (int i = 0; i < numberOfVertexes; ++i) {
        // scale
        vertexes[i].x = vertexes[i].x / xMax;
        vertexes[i].y = vertexes[i].y / yMax;
        vertexes[i].z = vertexes[i].z / zMax;
    }
}

void scaleBezier() {
    for (int i = 0; i < numberOfControlPoints; ++i) {
        cout << controlPolygon[i].x << " " << controlPolygon[i].y << " " << controlPolygon[i].z << endl;
        // center
        controlPolygon[i].x = controlPolygon[i].x - center.x;
        controlPolygon[i].y = controlPolygon[i].y - center.y;
        controlPolygon[i].z = controlPolygon[i].z - center.z;
    }

    xMax = xMax - center.x;
    yMax = yMax - center.y;
    zMax = zMax - center.z;

    for (int i = 0; i < numberOfControlPoints; ++i) {
        // scale
        controlPolygon[i].x = controlPolygon[i].x / xMax;
        controlPolygon[i].y = controlPolygon[i].y / yMax;
        controlPolygon[i].z = controlPolygon[i].z / zMax;
    }

    for (int i = 0; i < 100; ++i) {
        cout << bezierPoints[i].x << " " << bezierPoints[i].y << " " << bezierPoints[i].z << endl;
        // center
        bezierPoints[i].x = bezierPoints[i].x - center.x;
        bezierPoints[i].y = bezierPoints[i].y - center.y;
        bezierPoints[i].z = bezierPoints[i].z - center.z;
    }

    xMax = xMax - center.x;
    yMax = yMax - center.y;
    zMax = zMax - center.z;

    for (int i = 0; i < 100; ++i) {
        // scale
        bezierPoints[i].x = bezierPoints[i].x / xMax;
        bezierPoints[i].y = bezierPoints[i].y / yMax;
        bezierPoints[i].z = bezierPoints[i].z / zMax;
    }
}

void loadViewPoints() {
    std::ifstream infile("view_points.txt");

    float x, y, z;
    infile >> x >> y >> z;

//    // center
//    x = x - center.x;
//    y = y - center.y;
//    z = z - center.z;
//
//    // scale
//    x = x / xMax;
//    y = y / yMax;
//    z = z / zMax;

    glediste.x = x;
    glediste.y = y;
    glediste.z = z;

    infile >> x >> y >> z;

    ociste.x = x;
    ociste.y = y;
    ociste.z = z;
}

void myDisplay()
{
    printf("Pozvan myDispla()\n");
    glFlush();
}

void myReshape(int w, int h)
{
    printf("Pozvan myReshape()\n");
    int width = w, height = h;               //promjena sirine i visine prozora
    glViewport(0, 0, width, height);	//  otvor u prozoru

    glMatrixMode(GL_PROJECTION);		//	matrica projekcije
    glLoadIdentity();					//	jedinicna matrica
    gluOrtho2D(0, width, 0, height); 	//	okomita projekcija
    glMatrixMode(GL_MODELVIEW);			//	matrica pogleda
    glLoadIdentity();					//	jedinicna matrica

    glClearColor(1.0f, 1.0f, 1.0f, 0.0f); // boja pozadine
    glClear(GL_COLOR_BUFFER_BIT);		//	brisanje pozadine
    glPointSize(2.0);					//	postavi velicinu tocke za liniju
    glColor3f(0.0f, 0.0f, 0.0f);		//	postavi boju linije
}

void drawPolygon() {
    for (int i = 0; i < numberOfPolygons; ++i) {
        int a = polygons[i].a;
        int b = polygons[i].b;
        int c = polygons[i].c;

        glBegin(GL_LINES);
        {
            glVertex2i((vertexes[a].x + 2) * 10, 500 - (vertexes[a].y + 2) * 10);
            glVertex2i((vertexes[b].x + 2) * 10, 500 - (vertexes[b].y + 2) * 10);

            glVertex2i((vertexes[a].x + 2) * 10, 500 - (vertexes[a].y + 2) * 10);
            glVertex2i((vertexes[c].x + 2) * 10, 500 - (vertexes[c].y + 2) * 10);

            glVertex2i((vertexes[c].x + 2) * 10, 500 - (vertexes[c].y + 2) * 10);
            glVertex2i((vertexes[b].x + 2) * 10, 500 - (vertexes[b].y + 2) * 10);
        }
        glEnd();
    }

    cout << "drew all" << endl;
}

void calculateProjection() {
    float transformMat[4][4];
    transformMatrix(transformMat);
    printMat(transformMat);

    float perspectiveMat[4][4];
    float H = perspectiveMatrix(perspectiveMat);
    printMat(perspectiveMat);

    for (int i = 0; i < numberOfVertexes; ++i) {
        float vertexMat[1][4] = {{vertexes[i].x, vertexes[i].y, vertexes[i].z, 1}};
        float transformed[1][4];
        apply(vertexMat, transformMat, transformed);
        printf("%f %f %f -> %f %f %f %f\n", vertexes[i].x, vertexes[i].y, vertexes[i].z, transformed[0][0], transformed[0][1], transformed[0][2], transformed[0][3]);

        vertexes[i].x = transformed[0][0] * H / transformed[0][2] + 10;
        vertexes[i].y = transformed[0][1] * H / transformed[0][2] + 10;
        vertexes[i].z = transformed[0][2];
    }
}

void drawBezier() {
    for (int i = 0; i < numberOfControlPoints - 1; ++i) {
        glBegin(GL_LINES);
        {
            cout << (controlPolygon[i].x + 2) * 10 << " " << 500 - (controlPolygon[i].y + 2) * 10 << endl;

            glVertex2i((controlPolygon[i].x + 2) * 10, 500 - (controlPolygon[i].y + 2) * 10);
            glVertex2i((controlPolygon[i + 1].x + 2) * 10, 500 - (controlPolygon[i + 1].y + 2) * 10);
        }
        glEnd();
    }

    for (int i = 0; i < 100 - 1; ++i) {
        glBegin(GL_LINES);
        {
            cout << bezierPoints[i].x << " " << bezierPoints[i].y << endl;
            cout << (bezierPoints[i].x + 2) * 10 << " " << 500 - (bezierPoints[i].y + 2) * 10 << endl;

            glVertex2i((bezierPoints[i].x + 2) * 10, 500 - (bezierPoints[i].y + 2) * 10);
            glVertex2i((bezierPoints[i + 1].x + 2) * 10, 500 - (bezierPoints[i + 1].y + 2) * 10);
        }
        glEnd();
    }
}

void countBezier() {
    std::ifstream infile("bezier_round.txt");

    float x, y, z;
    while (infile >> x >> y >> z)
    {
        numberOfControlPoints++;
    }

    controlPolygon = new Vertex[numberOfControlPoints];
}

void loadBezier() {
    std::ifstream infile("bezier_round.txt");

    int currVertex = 0;

    float x, y, z;
    while (infile >> x >> y >> z)
    {
        Vertex v;
        v.x = x;
        v.y = y;
        v.z = z;

        controlPolygon[currVertex++] = v;
    }

//    center.x = xSum / numberOfVertexes;
//    center.y = ySum / numberOfVertexes;
//    center.z = zSum / numberOfVertexes;
}

void myKeyboard(unsigned char theKey, int mouseX, int mouseY)
{
    switch (theKey)
    {
        case 'd':
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            load();
            scale();

            ociste.y -= 0.1;
            ociste.x -= 0.1;
            glediste.y -= 0.5;
            glediste.x += 0.5;
            printVertex(ociste);
            printVertex(glediste);
            calculateProjection();
             drawPolygon();
            break;
        case 'f':
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            load();
            scale();

            ociste.y += 0.1;
            ociste.x += 0.1;
            glediste.y += 0.5;
            glediste.x -= 0.5;
            printVertex(ociste);
            printVertex(glediste);
            calculateProjection();
            drawPolygon();
            break;
        case 'b':
            cout << "nesto stisnbo b" << endl;
            drawBezier();
            break;
        case 'r':
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            load();
            scale();

            glediste.x = bezierPoints[bezierIdx].x;
            glediste.y = bezierPoints[bezierIdx].y;
            glediste.z = bezierPoints[bezierIdx].z;

            printVertex(ociste);
            printVertex(glediste);
            calculateProjection();
            drawPolygon();

            bezierIdx++;

            break;
        case 'l':
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            load();
            scale();

            glediste.x = bezierPoints[bezierIdx].x;
            glediste.y = bezierPoints[bezierIdx].y;
            glediste.z = bezierPoints[bezierIdx].z;

            printVertex(ociste);
            printVertex(glediste);
            calculateProjection();
            drawPolygon();

            bezierIdx--;

            break;
    }
    glFlush();
}

void draw(int argc, char ** argv) {
    glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
    glutInitWindowSize(800, 800);
    glutInitWindowPosition(100, 100);
    glutInit(&argc, argv);

    GLuint window = glutCreateWindow("Glut OpenGL Linija");
    glutReshapeFunc(myReshape);
    glutDisplayFunc(myDisplay);
    glutKeyboardFunc(myKeyboard);

    glutMainLoop();

};

int factorial(int n) {
    if (n == 0) {
        return 1;
    }

    int res = 1;
    for (int i = 1; i <= n; ++i) {
        res = res * i;
    }

    return res;
}

float bernstein(float t, int i, int n) {
    return float(factorial(n)) / float(factorial(i) * factorial(n - i)) * pow(t, i) * pow(1 - t, n - i);
}

void calculateBezierPoints() {
    for (int i = 0; i <= 100; i++) {
        Vertex v;
        v.x = 0; v.y = 0; v.z = 0;
        cout << "vertex calc before " << v.x << " " << v.y << endl;
        for (int j = 0; j < numberOfControlPoints; ++j) {
            float b = bernstein(float(i) / 100.f, j, numberOfControlPoints - 1);
            cout << float(i) / 100.f << ": " << b << " " << endl;
            v.x += controlPolygon[j].x * b;
            v.y += controlPolygon[j].y * b;
            v.z += controlPolygon[j].z * b;
        }
        cout << "vertex calc " << v.x << " " << v.y << endl;
        bezierPoints[i] = v;
    }

    cout << "bezier points calculated" << endl;
}

int main(int argc, char ** argv)
{
    count();
    vertexes = new Vertex[numberOfVertexes];
    polygons = new Polygon[numberOfPolygons];

    load();
    scale();

    loadViewPoints();

    printVertex(sub(ociste, glediste));

    //calculateProjection();

    countBezier();
    loadBezier();
    calculateBezierPoints();

//    scaleBezier();

    draw(argc, argv);
}
