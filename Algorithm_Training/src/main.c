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

void kFoldDisabled(void);
void kFoldEnabled(int kFoldNum);

int main(void)
{
    char **consoleOption = importFile("cgp_params2.txt");
    int kFoldIterations = atoi(consoleOption[3]);
    int isEnableKFold = atoi(consoleOption[4]);

    if(isEnableKFold == 0)
        kFoldDisabled();
    else
        kFoldEnabled(kFoldIterations);
}

void kFoldEnabled(int kFoldNum)
{
    /* CGP basic structure initialization */
	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct dataSet *overallData = NULL;
	struct chromosome *chromo = NULL;

	char dataTR[25] = "./01_training.csv";
	char dataVA[25] = "./02_validation.csv";
	char dataTE[25] = "./03_testing.csv";

	trainingData = initialiseDataSetFromFile(dataTR);
	validationData = initialiseDataSetFromFile(dataVA);
	testData = initialiseDataSetFromFile(dataTE);

	char **cgp_params = importFile("cgp_params.txt");
	char **cgp_params2 = importFile("cgp_params2.txt");

    /* Parse parameters from external text file */
	threshold = atof(cgp_params[0]);
	threshIncre = atof(cgp_params[1]);
	classNumber = atof(cgp_params[2]);

	int numInputs = atoi(cgp_params2[1]);
	int numNodes = atoi(cgp_params[3]);
	int numOutputs = atoi(cgp_params2[2]);
	int nodeArity = atoi(cgp_params[4]);

	params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

	int numGens = atoi(cgp_params[5]);
	double targetFitness = 0.1;
	int updateFrequency = atoi(cgp_params[6]);

	setFitnessFromText(strtok(cgp_params2[0],"\n"), params);

	/* As described by the function name */
	setRandomNumberSeed(atoi(cgp_params[7]));
	addNodeFunction(params, "add,sub,mul,div");
	setTargetFitness(params, targetFitness);
	setMutationRate(params, atof(cgp_params[8]));
	setShortcutConnections(params, 0);
	setUpdateFrequency(params, updateFrequency);

	printParameters(params);

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

	/* Save the chromosome in external file */
	saveChromosome(chromo, "latest_chromo.chromo");
	saveChromosomeDot(chromo, 0, "chromo.dot");

	/* Display test data execution result */

	//getBestEntity(cgp_params[7]);

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeParameters(params);
	freeChromosome(chromo);
	free(cgp_params);
	free(cgp_params2);

	int i = 0;
	char index[2];
	const char* dataOne = "/01_training.csv";
	const char* dataTwo = "/02_validation.csv";
	const char* dataThree = "/03_testing.csv";
	for(i = 0; i < kFoldNum; i++)
    {
        cgp_params = importFile("cgp_params.txt");
        cgp_params2 = importFile("cgp_params2.txt");
        char kFoldDataSrc[50] = "./kfolddata/fold_";
        char kFoldTR[50];
        char kFoldVA[50];
        char kFoldTE[50];

        sprintf(index,"%d",i);
        strcat(kFoldDataSrc,index);

        strcat(kFoldDataSrc,dataOne);
        strcpy(kFoldTR,kFoldDataSrc);

        strcpy(kFoldDataSrc,"./kfolddata/fold_");
        strcat(kFoldDataSrc,index);

        strcat(kFoldDataSrc,dataTwo);
        strcpy(kFoldVA,kFoldDataSrc);

        strcpy(kFoldDataSrc,"./kfolddata/fold_");
        strcat(kFoldDataSrc,index);

        strcat(kFoldDataSrc,dataThree);
        strcpy(kFoldTE,kFoldDataSrc);

        numInputs = atoi(cgp_params2[1]);
        numNodes = atoi(cgp_params[3]);
        numOutputs = atoi(cgp_params2[2]);
        nodeArity = atoi(cgp_params[4]);

        numGens = atoi(cgp_params[5]);
        targetFitness = 0.1;
        updateFrequency = atoi(cgp_params[6]);

        trainingData = initialiseDataSetFromFile(kFoldTR);
        validationData = initialiseDataSetFromFile(kFoldVA);
        testData = initialiseDataSetFromFile(kFoldTE);

        params = initialiseParameters(numInputs, numNodes, numOutputs, nodeArity);

        setRandomNumberSeed(atoi(cgp_params[7]));
        addNodeFunction(params, "add,sub,mul,div");
        setTargetFitness(params, targetFitness);
        setMutationRate(params, atof(cgp_params[8]));
        setShortcutConnections(params, 0);
        setUpdateFrequency(params, updateFrequency);
        setFitnessFromText(strtok(cgp_params2[0],"\n"), params);

        free(cgp_params);
        free(cgp_params2);

        chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

        /* Save the chromosome in external file */
        saveChromosome(chromo, "latest_chromo.chromo");
        saveChromosomeDot(chromo, 0, "chromo.dot");

        /* Display test data execution result */

        //getBestEntity(cgp_params[7]);

        freeDataSet(trainingData);
        freeDataSet(validationData);
        freeDataSet(testData);
        freeChromosome(chromo);
        freeParameters(params);


    }


}

void kFoldDisabled(void)
{
    /* CGP basic structure initialization */
	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct dataSet *overallData = NULL;
	struct chromosome *chromo = NULL;

	char **cgp_params = importFile("cgp_params.txt");
	char **cgp_params2 = importFile("cgp_params2.txt");

	/* Parse parameters from external text file */
	threshold = atof(cgp_params[0]);
	threshIncre = atof(cgp_params[1]);
	classNumber = atof(cgp_params[2]);

	int numInputs = atoi(cgp_params2[1]);
	int numNodes = atoi(cgp_params[3]);
	int numOutputs = atoi(cgp_params2[2]);
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
	overallData = initialiseDataSetFromFile("./overall.csv");

	char *kFoldDataSrc = "./kfolddata";

	setFitnessFromText(strtok(cgp_params2[0],"\n"), params);

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

	/* Save the chromosome in external file */
	saveChromosome(chromo, "latest_chromo.chromo");
	saveChromosomeDot(chromo, 0, "chromo.dot");

	/* Display test data execution result */
	char userInput;
	printf("Show test data result or overall result? (T/O):");
	scanf("%c",&userInput);
	if(userInput == 't' || userInput == 'T')
        setDisplayAction(strtok(cgp_params2[0],"\n"), chromo, testData);
    else if(userInput == 'o' || userInput == 'O')
        setDisplayAction(strtok(cgp_params2[0],"\n"), chromo, overallData);


	getBestEntity(cgp_params[7]);

	if(atoi(cgp_params2[4]) == 1)
		runKFold(params, numGens, atoi(cgp_params2[3]), strtok(cgp_params2[0],"\n"));

    free(cgp_params);
    free(cgp_params2);

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeChromosome(chromo);
	freeParameters(params);
    int i = 0;
	for(i = 0; i < 20000000; i++)
    {
        printf("%d\n",i);
    }
}

