#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "cgp-sls.h"
#include <time.h>
#include <gmp.h>
#include "fitness_functions.h"
#include <limits.h>

double threshIncre;
double threshold;
double classNumber;

mpf_t* mpfArrTest(void);
void idSortTest(void);
void stringCastTest(void);
void memoryLeakTest(void);
void chromoFileNameTest(void);
void stcConfidenceTest(void);

int main(void)
{
//
//    mpf_t* p = malloc(4*sizeof(mpf_t));
//    mpf_t k;
//    int i = 0;
//    mpf_init_set_ui(k,2);
//
//    for(i = 0; i < 4; i++)
//    {
//       mpf_init_set_ui(p[i],1);
//       mpf_pow_ui(p[i],k,i);
//       gmp_printf("%.Ff\n",p[i]);
//    }
    mpf_t* p = mpfArrTest();
    int i = 0;
    for(i = 0; i < 4; i++)
    {
        gmp_printf("%.Ff\n",p[i]);
    }


}

mpf_t* mpfArrTest(void)
{
    mpf_t *foo;
    foo = malloc(4*sizeof(mpf_t));

    int i ;
    for(i = 0; i < 4; i++)
    {
        mpf_init_set_ui(foo[i],INT_MAX*i);
    }

    return foo;

}

void stcConfidenceTest(void)
{
    int thresholdMargin[2] = {100,200};
    double chromoOutput = 120;
    double confidence = stcConfidence(thresholdMargin,chromoOutput);
    printf("%f%%\n",confidence*100);

}

void chromoFileNameTest(void)
{
    char **cgpArr = importFile("cgp_params.txt");
    char chromoFileName[30];
    strtok(cgpArr[3],"\n");
    strtok(cgpArr[4],"\n");
    strtok(cgpArr[8],"\n");
    double mutRate = atof(cgpArr[8]);

    /* Process the mutation rate entity, in case of identified as file extension */
    mutRate*=100;

    char mutRateChar[3];

    sprintf(mutRateChar, "%.f",mutRate);

    strcpy(chromoFileName,cgpArr[3]);
    strcat(chromoFileName,"_");
    strcat(chromoFileName,cgpArr[4]);
    strcat(chromoFileName,"_");
    strcat(chromoFileName,mutRateChar);
    strcat(chromoFileName,"_chromo.chromo");

    printf("%s\n",chromoFileName);

}

void memoryLeakTest(void)
{
    int i = 0;
    for(i = 0; i < 20000000; i++)
    {
        printf("%d\n",i);
    }
}

void stringCastTest(void)
{
    int i = 0;
    char path[20] = "./kfolddata/fold_";
    char index[2];

    for(i = 0; i < 10; i++)
    {
        strcpy(path,"./kfolddata/fold_");
        sprintf(index,"%d",i);
        strcat(path,index);

        printf("%s\n",path);
    }
}

void idSortTest(void)
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
