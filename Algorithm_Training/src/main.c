#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "cgp-sls.h"
#include <time.h>

#define MAX_UPDRS 56
#define CGP_PARAMS 12

double threshold = 10;
double threshIncre = 10;
double classNumber = 4;
double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double totalSum(struct parameters *params, struct chromosome *chromo, struct dataSet *data);

int maxIndex(double *arr);
char **importCGPParams(void);
void getBestEntity(void);

void stcAction(struct chromosome *chromo, struct dataSet *testData);
void ftcAction(struct chromosome *chromo, struct dataSet *testData);
void tsAction(struct chromosome *chromo, struct dataSet *testData);
void setDisplayAction(char *arr, struct chromosome *chromo, struct dataSet *testData);
void setFitnessFromText(char *arr, struct parameters *params);

int main(void)
{

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	char **cgp_params = importCGPParams();
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
	
	setFitnessFromText(strtok(cgp_params[11],"\n"), params);

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

	saveChromosome(chromo, "latest_chromo.chromo");

	saveChromosomeDot(chromo, 0, "chromo.dot");

	setDisplayAction(strtok(cgp_params[11],"\n"), chromo, testData);

	getBestEntity();

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}

int maxIndex(double *arr)
{
	int i = 0;
	int max = 0;
	double tmp = arr[0];

	for (i = 0; i < 4; i++)
	{
		if (tmp < arr[i])
		{
			tmp = arr[i];
			max = i;
		}
	}

	return max;
}

double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	int i;
	double threshError = 0;

	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double *chromoOutput = malloc(4 * sizeof(double));
		double *expectedOutput = getDataSetSampleOutputs(data, i);

		int j = 0;
		for (j = 0; j < 4; j++)
		{
			chromoOutput[j] = getChromosomeOutput(chromo, j);
			//threshError += pow(chromoOutput[j] - expectedOutput[j],2);
		}

		if (maxIndex(chromoOutput) != maxIndex(expectedOutput))
			threshError++;
	}

	return threshError / (getNumDataSetSamples(data));
}

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	int i;
	double threshError = 0;

	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{

		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedClass = getDataSetSampleOutputs(data, i)[0];
		if (chromoOutput < threshold + (expectedClass - 1) * threshIncre || chromoOutput >= threshold + expectedClass * threshIncre)
		{
			threshError++;
		}
	}

	return threshError / (getNumDataSetSamples(data));
}

double totalSum(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	int i = 0;
	double totalSum = 0;

	if (getNumChromosomeInputs(chromo) != getNumDataSetInputs(data))
	{
		printf("Error: the number of chromosome inputs must match the number of inputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	if (getNumChromosomeOutputs(chromo) != getNumDataSetOutputs(data))
	{
		printf("Error: the number of chromosome outputs must match the number of outputs specified in the dataSet.\n");
		printf("Terminating.\n");
		exit(0);
	}

	double maxPossibleSum = 56 * getNumDataSetSamples(data);

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo, 0);
		double expectedOutput = getDataSetSampleOutputs(data, i)[0];

		totalSum += fabs(expectedOutput - chromoOutput);
	}

	return totalSum / maxPossibleSum;
}

char **importCGPParams(void)
{
	FILE *fp;
	char line[256];
	int i = 0;

	char **cgp_params = malloc(CGP_PARAMS * sizeof(char *));

	fp = fopen("cgp_params.txt", "r");

	if (fp == NULL)
	{
		printf("File not found");
		return 0;
	}

	for (i = 0; i < CGP_PARAMS; i++)
	{
		cgp_params[i] = malloc(10 * sizeof(char));
	}
	i = 0;

	while (fgets(line, sizeof(line), fp))
	{
		strcpy(cgp_params[i], line);
		i++;
	}

	fclose(fp);

	return cgp_params;
}

void getBestEntity(void)
{
	FILE *fp;
	char line[256];
	int i = 0;

	double tmpBest[4] = {0, 0, 0, 0};

	fp = fopen("CGP_Output.txt", "r");
	if (fp == NULL)
	{
		printf("File not found.");
		return 0;
	}

	while (fgets(line, sizeof(line), fp))
	{
		i = 0;
		char *pch = strtok(line, " ");
		char **fitnessSeg = malloc(4 * sizeof(char *));

		while (pch != NULL)
		{
			fitnessSeg[i] = malloc(10 * sizeof(char));
			strcpy(fitnessSeg[i], pch);
			pch = strtok(NULL, " ");
			i++;
		}
		if (atof(fitnessSeg[1]) >= atof(fitnessSeg[2]))
		{
			if (atof(fitnessSeg[2]) > tmpBest[2])
			{
				if (atof(fitnessSeg[3]) > tmpBest[3])
				{
					for (i = 0; i < 4; i++)
					{
						tmpBest[i] = atof(fitnessSeg[i]);
					}
				}
			}
		}
	}
	printf("\nBest gen at %.0f with fitness of %.2f, %.2f and %.2f.\n", tmpBest[0], tmpBest[1], tmpBest[2], tmpBest[3]);
}

void stcAction(struct chromosome *chromo, struct dataSet *testData){
	int i;
	int mismatchError = 0;
	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{

		executeChromosome(chromo, getDataSetSampleInputs(testData, i));
		double chromoOutput = 0;

		chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedOutput = getDataSetSampleOutputs(testData, i)[0];
		printf("%.2f ", chromoOutput);

        if (chromoOutput <= threshold+(expectedOutput-1)*threshIncre || chromoOutput > threshold+expectedOutput*threshIncre){
            printf("Mismatch ");
            mismatchError++;
        }
        else{
            printf("Match ");
        }

		printf(" %.2f", getDataSetSampleOutputs(testData, i)[0]);
		printf("\n");
	}

	printf("Accuracy = %.4f (%d/%d)", 100 - ((float)mismatchError * 100 / getNumDataSetSamples(testData)), (getNumDataSetSamples(testData) - mismatchError), getNumDataSetSamples(testData));

	printf("\n");
}

void ftcAction(struct chromosome *chromo, struct dataSet *testData){
	int i;
	int mismatchError = 0;
	for(i = 0;i < getNumDataSetSamples(testData);i++){
		executeChromosome(chromo, getDataSetSampleInputs(testData,i));
		double *chromoOutput = malloc(4*sizeof(double));
		double *expectedOutput = getDataSetSampleOutputs(testData,i);
		int j = 0;
		for(j = 0; j < 4; j++){
            chromoOutput[j] = getChromosomeOutput(chromo,j);
			printf("%.2f ", chromoOutput[j]);
		}
		if(maxIndex(chromoOutput) != maxIndex(expectedOutput)){
			printf("Mismatch ");
			mismatchError++;
		}

		else
			printf("Match ");

		for(j = 0; j < 4; j++){
			printf("%.2f ", expectedOutput[j]);
		}

		printf("\n");

	}
    printf("Accuracy = %.4f (%d/%d)", 100 - ((float)mismatchError * 100 / getNumDataSetSamples(testData)), (getNumDataSetSamples(testData) - mismatchError), getNumDataSetSamples(testData));
}

void tsAction(struct chromosome *chromo, struct dataSet *testData){
	int i;
	for (i = 0; i < getNumDataSetSamples(testData); i++)
	{

		executeChromosome(chromo, getDataSetSampleInputs(testData, i));
		double chromoOutput = 0;

		chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedOutput = getDataSetSampleOutputs(testData, i)[0];
		printf("%.2f ", chromoOutput);

		printf("Diff: %.2f ", fabs(chromoOutput - expectedOutput));

		printf(" %.2f", getDataSetSampleOutputs(testData, i)[0]);
		printf("\n");
	}


	printf("\n");
}

void setFitnessFromText(char *arr, struct parameters *params){
	if(strcmp(arr,"STC") == 0){
		setCustomFitnessFunction(params, simpleThresholdClassifier, "STC");
		
	}
	else if(strcmp(arr,"FTC") == 0){
		setCustomFitnessFunction(params, fourOutputFitnessFunction, "FTC");
		//ftcAction(chromo, testData);
	}
	else if (strcmp(arr,"TS") == 0){
		setCustomFitnessFunction(params, totalSum, "TS");
		//tsAction(chromo, testData);
	}
		
}

void setDisplayAction(char *arr, struct chromosome *chromo, struct dataSet *testData){
	if(strcmp(arr,"STC") == 0){
		stcAction(chromo, testData);
	}
	else if(strcmp(arr,"FTC") == 0){
		ftcAction(chromo, testData);
	}
	else if (strcmp(arr,"TS") == 0){
		tsAction(chromo, testData);
	}
}
