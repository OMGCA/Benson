#include <stdio.h>
#include <stdlib.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;


int main(void)
{

    int i = 0;
    char **controlData = importFile("controlConsole.csv");
    char **controlID = malloc(53 * sizeof(char*));
    for(i = 0; i < 53; i++)
    {
        controlID[i] = (char*)malloc(10*sizeof(char));
    }

    double *controlDataArr = malloc(53 * sizeof(double));

    for(i = 0; i < 53; i++)
    {
        char* tmpID = (char*)strSplit(controlData[i],",",0);
        strcpy(controlID[i],tmpID);
        free(tmpID);
        //controlID[i] = strSplit(controlData[i],",",0);
        char* tmpData = strSplit(controlData[i],",",1);
        controlDataArr[i] = atof(tmpData);
        free(tmpData);
    }

    insertionSort(controlDataArr,controlID,53);

    for(i = 0; i < 53; i++)
    {
        printf("%s %.2f\n", controlID[i], controlDataArr[i]);
    }

    printf("%s\n", controlID[binarySearch(controlDataArr,53,35450.0)]);

    for(i = 0; i < 53; i++)
    {
        free(controlData[i]);
        free(controlID[i]);
    }
    free(controlData);
    free(controlID);
    free(controlDataArr);

}
