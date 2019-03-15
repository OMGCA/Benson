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

int main(void)
{
	/* CGP basic structure initialization */
	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	char **cgp_params = importCGPParams();

	/* Parse parameters from external text file */
	threshold = atof(cgp_params[0]);
	threshIncre = atof(cgp_params[1]);
	classNumber = atof(cgp_params[2]);

	int numInputs = atoi(cgp_params[9]);
	int numNodes = atoi(cgp_params[3]);
	int numOutputs = atoi(cgp_params[10]);
	int nodeArity = atoi(cgp_params[4]);

	int numGens = atoi(cgp_params[5]);
	double targetFitness = 0.1;
	int updateFrequency = atoi(cgp_params[6]);

	/* As described by the function name */
	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

	setRandomNumberSeed(atoi(cgp_params[7]));
	addNodeFunction(params, "add,sub,mul,div");
	setTargetFitness(params, targetFitness);
	setMutationRate(params, atof(cgp_params[8]));
	setShortcutConnections(params, 0);
	setUpdateFrequency(params, updateFrequency);

	printParameters(params);

	// Note: you may need to check this path such that it is relative to your executable
	trainingData = initialiseDataSetFromFile("./01_training.csv");
	validationData = initialiseDataSetFromFile("./02_validation.csv");
	testData = initialiseDataSetFromFile("./03_testing.csv");

	char *kFoldDataSrc = "./kfolddata";

	setFitnessFromText(strtok(cgp_params[11],"\n"), params);

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

	/* Save the chromosome in external file */
	saveChromosome(chromo, "latest_chromo.chromo");
	saveChromosomeDot(chromo, 0, "chromo.dot");

	/* Display test data execution result */
	setDisplayAction(strtok(cgp_params[11],"\n"), chromo, testData);

	getBestEntity();

	/*struct chromosome* kFoldChromo[10];
	struct dataSet* kFoldTraining[10];
	struct dataSet* kFoldValidation[10];
	struct dataSet* kFoldTest[10];

	int i = 0;
	for(i = 0; i < 10; i++){
        kFoldChromo[i] = NULL;
        kFoldTraining[i] = NULL;
        kFoldValidation[i] = NULL;
        kFoldTest[i] = NULL;
        char foldIndex[10];
        char nFold[80];
        strcpy(nFold, kFoldDataSrc);
        strcat(nFold,"/fold_");
        itoa(i,foldIndex,10);
        strcat(nFold,foldIndex);

        char foldTrain[80];
        strcpy(foldTrain, nFold);

        char foldValidate[80];
        strcpy(foldValidate, nFold);

        char foldTest[80];
        strcpy(foldTest, nFold);

        strcat(foldTrain, "/01_training.csv");
        strcat(foldValidate, "/02_validation.csv");
        strcat(foldTest, "/03_testing.csv");

        kFoldTraining[i] = initialiseDataSetFromFile(foldTrain);
        kFoldValidation[i] = initialiseDataSetFromFile(foldValidate);
        kFoldTest[i] = initialiseDataSetFromFile(foldTest);

        kFoldChromo[i] = runValiTestCGP(params, kFoldTraining[i], kFoldValidation[i], kFoldTest[i], numGens);

        printChromosome(kFoldChromo[i], 0);

        saveChromosome(kFoldChromo[i], "latest_chromo.chromo");
        saveChromosomeDot(kFoldChromo[i], 0, "chromo.dot");

        //setDisplayAction(strtok(cgp_params[11],"\n"), chromo, testData);

        getBestEntity();

        //printf("%s\n%s\n%s\n",foldTrain, foldValidate, foldTest);
        freeDataSet(kFoldTraining[i]);
        freeDataSet(kFoldValidation[i]);
	}

	for(i = 0; i < 10; i++){
        printf("\nIteration %d: \n", i);
        setDisplayAction(strtok(cgp_params[11],"\n"), kFoldChromo[i], kFoldTest[i]);
        freeChromosome(kFoldChromo[i]);
        freeDataSet(kFoldTest[i]);
	}*/

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}

