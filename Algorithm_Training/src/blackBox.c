#include <stdio.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;


int main(void)
{
    double testArr[] = {0.65,0.23,3243.3,0.22,1.233};
    char **testArr2 = malloc(5*sizeof(char*));
    int i = 0;
    testArr2[0] = "GG";
    testArr2[1] = "AA";
    testArr2[2] = "UU";
    testArr2[3] = "KK";
    testArr2[4] = "SS";
    insertionSort(testArr,testArr2,sizeof(testArr)/sizeof(double));

    for(i = 0; i < sizeof(testArr)/sizeof(double); i++)
    {
        printf("%.2f %s\n",testArr[i], testArr2[i]);
    }
}
