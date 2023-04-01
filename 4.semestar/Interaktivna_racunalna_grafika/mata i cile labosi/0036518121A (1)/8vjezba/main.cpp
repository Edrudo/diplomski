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

void mandelbrot(int w, int h);
void julijev(int w, int h);

bool isMandelbrot;

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

    cout << isMandelbrot << endl;
    if (isMandelbrot) {
        mandelbrot(w, h);
    } else {
        julijev(w, h);
    }
}

void draw(int argc, char ** argv) {
    glutInitDisplayMode(GLUT_SINGLE | GLUT_RGB);
    glutInitWindowSize(400, 400);
    glutInitWindowPosition(100, 100);
    glutInit(&argc, argv);

    GLuint window = glutCreateWindow("Glut OpenGL Linija");
    glutReshapeFunc(myReshape);
    glutDisplayFunc(myDisplay);

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

void drawVertex(int x, int y, int color) {
    switch (color) {
        case 0:
            glColor3f(0.0f, 0.0f, 0.0f);
            break;
        case 1:
            glColor3f(1.0f, 0.0f, 0.0f);
            break;
        case 2:
            glColor3f(0.0f, 1.0f, 0.0f);
            break;
        case 3:
            glColor3f(0.0f, 0.0f, 1.0f);
            break;
        case 4:
            glColor3f(1.0f, 1.0f, 0.0f);
            break;
        case 5:
            glColor3f(0.0f, 1.0f, 1.0f);
            break;
        case 6:
            glColor3f(1.0f, 0.0f, 1.0f);
            break;
        case 7:
            glColor3f(1.0f, 1.0f, 1.0f);
            break;
        case 8:
            glColor3f(0.5f, 0.0f, 0.0f);
            break;
        case 9:
            glColor3f(1.0f, 0.5f, 0.0f);
            break;
        case 10:
            glColor3f(0.0f, 1.0f, 0.5f);
            break;
        case 11:
            glColor3f(0.0f, 0.5f, 1.0f);
            break;
        case 12:
            glColor3f(1.0f, 1.0f, 0.5f);
            break;
        case 13:
            glColor3f(0.5f, 1.0f, 1.0f);
            break;
        case 14:
            glColor3f(1.0f, 0.5f, 1.0f);
            break;
        case 15:
            glColor3f(1.0f, 0.8f, 1.0f);
            break;
    }

    glBegin(GL_POINTS);
    {
        glVertex2i(x, y);
    }
    glEnd();
}

void julijev(int w, int h) {
    float eps = 100;
    int m = 15;

    float uMin = -1, uMax = 1;
    float vMin = -1.2, vMax = 1.2;
    float cReal = 0.32, cImag = 0.043;

    int width = w, height = h;

    for (int px = 0; px < width; ++px) {
        for (int py = 0; py < height; ++py) {
            float u0 = ((uMax - uMin) / float(width)) * float(px) + uMin;
            float v0 = ((vMax - vMin) / float(height)) * float(py) + vMin;
            float zReal = u0, zImag = v0;

            int k = -1;
            float r = 0;

            while (r < eps && k < m) {
                k = k + 1;
                //float zRealTemp = x * x - y * y + x0;
                float zRealTemp = zReal * zReal - zImag * zImag + cReal;
                zImag = 2 * zReal * zImag + cImag;
                zReal = zRealTemp;

                r = sqrt(pow(zReal, 2) + pow(zImag, 2));
            }

            drawVertex(px, py, k);
        }
    }
}

void mandelbrot(int w, int h) {
    float eps = 100;
    int m = 15;

    float uMin = -2, uMax = 0.47;
    float vMin = -1.12, vMax = 1.12;

    int width = w, height = h;

    for (int px = 0; px < width; ++px) {
        for (int py = 0; py < height; ++py) {
            float u0 = ((uMax - uMin) / float(width)) * float(px) + uMin;
            float v0 = ((vMax - vMin) / float(height)) * float(py) + vMin;
            float zReal = 0, zImag = 0;

            int k = -1;
            float r = 0;

            while (r < eps && k < m) {
                k = k + 1;
                //float zRealTemp = x * x - y * y + x0;
                float zRealTemp = zReal * zReal - zImag * zImag + u0;
                zImag = 2 * zReal * zImag + v0;
                zReal = zRealTemp;

                r = sqrt(pow(zReal, 2) + pow(zImag, 2));
            }

            drawVertex(px, py, k);
        }
    }
}

int main(int argc, char ** argv)
{
    if (strcmp(argv[1], "mandelbrot") == 0) {
        isMandelbrot = true;
    } else {
        isMandelbrot = false;
    }

    draw(argc, argv);
}
