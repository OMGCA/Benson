#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "cgp-sls.h"
#include <time.h>
#include "fitness_functions.h"

double threshIncre;
double threshold;
double classNumber;

void cgpExecute(void);

int main(void)
{
    cgpExecute();
}

void cgpExecute(void)
{
    /* CGP basic structure initialization */
	struct parameters   *params = NULL;
	struct chromosome   *chromo = NULL;
	struct dataSet      *trainingData = NULL;
	struct dataSet      *validationData = NULL;
	struct dataSet      *testData = NULL;

    char    dataTR[50], dataVA[50], dataTE[50];
	char**  cgp_params  = importFile("cgp_params.txt");
	char**  cgp_params2 = importFile("cgp_params2.txt");

	/* Parse parameters from external text file */
	threshold   = atof(cgp_params[0]);
	threshIncre = atof(cgp_params[1]);
	classNumber = atof(cgp_params[2]);

	int numNodes    = atoi(cgp_params[3]);
	int nodeArity   = atoi(cgp_params[4]);
	int numGens     = atoi(cgp_params[5]);
	int updateFreq  = atoi(cgp_params[6]);

	int numInputs   = atoi(cgp_params2[1]);
	int numOutputs  = atoi(cgp_params2[2]);

	int kFoldIndex  = atoi(cgp_params[9]);

	double targetFitness = 0.1;

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

	setRandomNumberSeed(atoi(cgp_params[7]));
	addNodeFunction(params, "add, sub, mul, div");
	setTargetFitness(params, targetFitness);
	setMutationRate(params, atof(cgp_params[8]));
	setShortcutConnections(params, 0);
	setUpdateFrequency(params, updateFreq);

    if(kFoldIndex != 0)
    {
        char index[1];
        sprintf(index,"%d",kFoldIndex-1);

        char kFoldDataSrc[50] = "./kfolddata/fold_";
        strcat(kFoldDataSrc,index);
        strcat(kFoldDataSrc,"/01_training.csv");
        strcpy(dataTR, kFoldDataSrc);

        strcpy(kFoldDataSrc,"./kfolddata/fold_");
        strcat(kFoldDataSrc,index);
        strcat(kFoldDataSrc,"/02_validation.csv");
        strcpy(dataVA, kFoldDataSrc);

        strcpy(kFoldDataSrc,"./kfolddata/fold_");
        strcat(kFoldDataSrc,index);
        strcat(kFoldDataSrc,"/03_testing.csv");
        strcpy(dataTE, kFoldDataSrc);
    }
    else
    {
        strcpy(dataTR,"./01_training.csv");
        strcpy(dataVA,"./02_validation.csv");
        strcpy(dataTE,"./03_testing.csv");
    }
    printParameters(params);

    trainingData    = initialiseDataSetFromFile(dataTR);
    validationData  = initialiseDataSetFromFile(dataVA);
    testData        = initialiseDataSetFromFile(dataTE);

    setFitnessFromText(strtok(cgp_params2[0],"\n"), params);

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

    /* Save the chromosome in external file */
	saveChromosome(chromo, "latest_chromo.chromo");
	saveChromosomeDot(chromo, 0, "chromo.dot");

    getBestEntity(cgp_params[7]);

	free(cgp_params);
    free(cgp_params2);

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);

	freeChromosome(chromo);
	freeParameters(params);
}
