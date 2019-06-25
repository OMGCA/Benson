#include <stdio.h>
#include <stdlib.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;


int main(void)
{

    int i = 0;
    char **controlData = importFile("dataSet.csv");
    printf("%d\n",(int)sizeof(controlData));
    int entries = 163;
    char **controlID = malloc(entries * sizeof(char*));
    for(i = 0; i < entries; i++)
    {
        controlID[i] = (char*)malloc(10*sizeof(char));
    }

    double *controlDataArr = malloc(entries * sizeof(double));

    for(i = 0; i < entries; i++)
    {
        char* tmpID = (char*)strSplit(controlData[i],",",0);
        strcpy(controlID[i],tmpID);
        free(tmpID);

        char* tmpData = strSplit(controlData[i],",",1);
        controlDataArr[i] = atof(tmpData);
        free(tmpData);
    }

    insertionSort(controlDataArr,controlID,entries);

    for(i = 0; i < entries; i++)
    {
        printf("%s %.2f\n", controlID[i], controlDataArr[i]);
    }

    printf("%s\n", controlID[binarySearch(controlDataArr,entries,43238.0)]);

    for(i = 0; i < entries; i++)
    {
        free(controlData[i]);
        free(controlID[i]);
    }
    free(controlData);
    free(controlID);
    free(controlDataArr);

}
