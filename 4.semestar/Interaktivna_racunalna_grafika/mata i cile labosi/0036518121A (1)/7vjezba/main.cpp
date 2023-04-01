//*************************************************************************************************************
//	Crtanje tijela
//	Tijelo se crta u funkciji myObject
//
//	Zadatak: Treba ucitati tijelo zapisano u *.obj, sjencati i ukloniti staznje poligone
//	S tastature l - pomicanje ocista po x osi +
//		k - pomicanje ocista po x osi +
//              r - pocetni polozaj
//              esc izlaz iz programa
//*************************************************************************************************************

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

//*********************************************************************************
//	Pokazivac na glavni prozor i pocetna velicina.
//*********************************************************************************

GLuint window;
GLuint width = 800, height = 800;
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

Vertex *vertexesNorms;

Vertex test;

Vertex glediste;
Vertex ociste = {0, 0 ,5.0};
Vertex lightSource = {3, -4, 3};

float Ia = 50;
float ka = 0.4;
float Ii = 250;
float kd = 0.8;

//*********************************************************************************
//	Function Prototypes.
//*********************************************************************************

void myDisplay		();
void myReshape		(int width, int height);
void myMouse		(int button, int state, int x, int y);
void myKeyboard		(unsigned char theKey, int mouseX, int mouseY);
void myObject		();
void redisplay_all	(void);
void updatePerspective ();
void count();
void load();
void scale();
Vertex sub(Vertex a, Vertex b);
void printVertex(Vertex v);
void calculateVertexNorm();

//*********************************************************************************
//	Glavni program.
//*********************************************************************************

int main(int argc, char** argv)
{
    count();
    vertexes = new Vertex[numberOfVertexes];
    vertexesNorms = new Vertex[numberOfVertexes];
    polygons = new Polygon[numberOfPolygons];

    load();
    scale();

//    loadViewPoints();

    printVertex(sub(ociste, glediste));
    calculateVertexNorm();

    cout << "pre" << endl;
    for (int i = 0; i < numberOfVertexes; ++i) {
        printVertex(vertexesNorms[i]);
    }
    cout << "posle" << endl;

    //calculateProjection();

//    countBezier();
//    loadBezier();
//    calculateBezierPoints();

    // postavljanje dvostrukog spremnika za prikaz (zbog titranja)
    glutInitDisplayMode (GLUT_RGB | GLUT_DOUBLE);
    glutInitWindowSize(width, height);
    glutInitWindowPosition (100, 100);
    glutInit(&argc, argv);

    window = glutCreateWindow ("Tijelo");
    glutReshapeFunc(myReshape);
    glutDisplayFunc(myDisplay);
    glutMouseFunc(myMouse);
    glutKeyboardFunc(myKeyboard);
    printf("Tipka: l - pomicanje ocista po x os +\n");
    printf("Tipka: k - pomicanje ocista po x os -\n");
    printf("Tipka: s - pomicanje ocista po y os +\n");
    printf("Tipka: d - pomicanje ocista po y os -\n");
    printf("Tipka: g - pomicanje ocista po z os +\n");
    printf("Tipka: h - pomicanje ocista po z os -\n");
    printf("Tipka: r - pocetno stanje\n");
    printf("esc: izlaz iz programa\n");

    glutMainLoop();
    return 0;
}

// region prev
// region utils

void printVertex(Vertex v) {
    printf("vertex: %f %f %f\n", v.x, v.y, v.z);
}

Vertex add(Vertex a, Vertex b) {
    Vertex v;

    v.x = a.x + b.x;
    v.y = a.y + b.y;
    v.z = a.z + b.z;

    return v;
}

Vertex sub(Vertex a, Vertex b) {
    Vertex v;

    v.x = a.x - b.x;
    v.y = a.y - b.y;
    v.z = a.z - b.z;

    return v;
}

Vertex getNorm(int polygon) {
    int a = polygons[polygon].a;
    int b = polygons[polygon].b;
    int c = polygons[polygon].c;

    Vertex ab = sub(vertexes[a], vertexes[b]);
    Vertex ac = sub(vertexes[a], vertexes[c]);

    Vertex out;

    out.x = ab.y * ac.z - ab.z * ac.y;
    out.y = - ab.x * ac.z + ab.z * ac.x;
    out.z = ab.x * ac.y - ab.y * ac.x;

    return out;
}

float amp(Vertex v) {
    return sqrt(pow(v.x, 2) + pow(v.y, 2) + pow(v.z, 2));
}

Vertex normVector(Vertex v) {
    Vertex out;
    float vAmp = amp(v);

    out.x = v.x / vAmp;
    out.y = v.y / vAmp;
    out.z = v.z / vAmp;

    return out;
}

float dot(Vertex a, Vertex b) {
    return a.x * b.x + a.y * b.y + a.z * b.z;
}

// endregion

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

// endregion

//*********************************************************************************
//	Osvjezavanje prikaza.
//*********************************************************************************

void myDisplay(void)
{
    // printf("Pozvan myDisplay()\n");
    glClearColor( 1.0f, 1.0f, 1.0f, 1.0f);		         // boja pozadine - bijela
    glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    myObject();
    glutSwapBuffers();      // iscrtavanje iz dvostrukog spemnika (umjesto glFlush)
}

//*********************************************************************************
//	Promjena velicine prozora.
//	Funkcija gluPerspective i gluLookAt se obavlja
//		transformacija pogleda i projekcija
//*********************************************************************************

void myReshape (int w, int h)
{
    // printf("MR: width=%d, height=%d\n",w,h);
    width=w; height=h;
    glViewport (0, 0, width, height);
    updatePerspective();
}

void updatePerspective()
{
    glMatrixMode (GL_PROJECTION);        // aktivirana matrica projekcije
    glLoadIdentity ();
    gluPerspective(45.0, (float)width/height, 0.5, 8.0); // kut pogleda, x/y, prednja i straznja ravnina odsjecanja
    glMatrixMode (GL_MODELVIEW);         // aktivirana matrica modela
    glLoadIdentity ();
    gluLookAt (ociste.x, ociste.y, ociste.z, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);	// ociste x,y,z; glediste x,y,z; up vektor x,y,z
}

//*********************************************************************************
//	Crta moj objekt. Ovdje treba naciniti prikaz ucitanog objekta.
//*********************************************************************************

void calculateVertexNorm() {
    for (int vertex = 0; vertex < numberOfVertexes; ++vertex) {
        int counter = 0;
        Vertex sum;

        for (int i = 0; i < numberOfPolygons; ++i) {
            if (polygons[i].a == vertex || polygons[i].b == vertex || polygons[i].c == vertex) {
                sum = add(sum, getNorm(i));
                counter++;
            }
        }

        sum.x = sum.x / float(counter);
        sum.y = sum.y / float(counter);
        sum.z = sum.z / float(counter);

        vertexesNorms[vertex] = normVector(sum);
    }
}

void myObject ()
{
    for (int i = 0; i < numberOfPolygons; ++i) {
        int a = polygons[i].a;
        int b = polygons[i].b;
        int c = polygons[i].c;


        Vertex norm = getNorm(i);
        Vertex toObjectVector = sub(glediste, ociste);

        if (acos(dot(norm, toObjectVector) / (amp(norm) * amp(toObjectVector))) * 180./3.14159 > 90) {
            // ploha je straznja
            continue;
        }

        Vertex lightSourceVec = sub( lightSource, glediste);

        GLubyte Ida = Ia * ka + Ii * kd * dot(normVector(lightSourceVec), normVector(vertexesNorms[a]));
        GLubyte Idb = Ia * ka + Ii * kd * dot(normVector(lightSourceVec), normVector(vertexesNorms[b]));
        GLubyte Idc = Ia * ka + Ii * kd * dot(normVector(lightSourceVec), normVector(vertexesNorms[c]));

//      konstantno sjencanje
//      float Id = Ia * ka + Ii * kd * dot(normVector(lightSourceVec), normVector(norm));
//      cout << "color: " << Id << " " << amp(normVector(lightSourceVec)) << " " << amp(normVector(norm)) << " " << dot(normVector(lightSourceVec), normVector(norm)) << endl;
//      glColor3ub(Id, 0, 0);

        glBegin (GL_TRIANGLES);
        {
            glColor3ub(Ida, 0, 0); glVertex3f(vertexes[a].x, vertexes[a].y, vertexes[a].z);
            glColor3ub(Idb, 0, 0); glVertex3f(vertexes[b].x, vertexes[b].y, vertexes[b].z);
            glColor3ub(Idc, 0, 0); glVertex3f(vertexes[c].x, vertexes[c].y, vertexes[c].z);
        }
        glEnd();

        glColor3ub(0, 0, 0);
        glBegin (GL_LINE_LOOP);
        {
            glVertex3f(vertexes[a].x, vertexes[a].y, vertexes[a].z);
            glVertex3f(vertexes[b].x, vertexes[b].y, vertexes[b].z);
            glVertex3f(vertexes[c].x, vertexes[c].y, vertexes[c].z);
        }
        glEnd();
    }
}

//*********************************************************************************
//	Mis.
//*********************************************************************************

void myMouse(int button, int state, int x, int y)
{
    //	Desna tipka - brise canvas.
    if (button == GLUT_RIGHT_BUTTON && state == GLUT_DOWN)
    {
        ociste.x=0;
        updatePerspective();
        glutPostRedisplay();
    }
}

//*********************************************************************************
//	Tastatura tipke - esc - izlazi iz programa.
//*********************************************************************************

void myKeyboard(unsigned char theKey, int mouseX, int mouseY)
{
    switch (theKey)
    {
        case 'l': ociste.x = ociste.x+0.1;
            break;

        case 'k': ociste.x =ociste.x-0.1;
            break;

        case 's': ociste.y = ociste.y+0.1;
            break;

        case 'd': ociste.y =ociste.y-0.1;
            break;

        case 'r': ociste.x=0.0;
            break;

        case 'g': ociste.z = ociste.z+0.1;
            break;

        case 'h': ociste.z =ociste.z-0.1;
            break;

        case 27:  exit(0);
            break;
    }
    updatePerspective();
    glutPostRedisplay();
}
