#include <stdio.h>
#include <stdlib.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;


int main(void)
{
//    double testArr[] = {0.65,0.23,3243.3,0.22,1.233};
//    char **testArr2 = malloc(5*sizeof(char*));
//    int i = 0;
//    testArr2[0] = "GG";
//    testArr2[1] = "AA";
//    testArr2[2] = "UU";
//    testArr2[3] = "KK";
//    testArr2[4] = "SS";
//    insertionSort(testArr,testArr2,sizeof(testArr)/sizeof(double));
//
//    for(i = 0; i < sizeof(testArr)/sizeof(double); i++)
//    {
//        printf("%.2f %s\n",testArr[i], testArr2[i]);
//    }
    int i = 0;
    char **controlData = importFile("controlConsole.csv",53);
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

        printf("%s %.2f\n",controlID[i], controlDataArr[i]);
    }

    for(i = 0; i < 53; i++)
    {
        free(controlData[i]);
        free(controlID[i]);
    }
    free(controlData);
    free(controlID);
    free(controlDataArr);

}
