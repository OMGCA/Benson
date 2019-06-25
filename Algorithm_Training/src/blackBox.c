#include <stdio.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;


int main(void)
{
    double testArr[] = {0.65,0.23,3243.3,0.22,1.233};
    insertionSort(testArr,sizeof(testArr)/sizeof(double));
    int i = 0;
    for(i = 0; i < sizeof(testArr)/sizeof(double); i++)
    {
        printf("%.2f\n",testArr[i]);
    }
}
