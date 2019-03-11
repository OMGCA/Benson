#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "cgp-sls.h"
#include <time.h>
#include "fitness_functions.h"
/* Find the maximum output for multi-output data */
/* Used for FTC */
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
	/* Routine check */
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

	int i;

	/* Counter to keep a record of error matches */
	double threshError = 0;

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		/* Get the chromosome output */
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double *chromoOutput = malloc(4 * sizeof(double));
		int j = 0;
		for (j = 0; j < 4; j++)
		{
			chromoOutput[j] = getChromosomeOutput(chromo, j);
		}

		/* Get acutal output */
		double *expectedOutput = getDataSetSampleOutputs(data, i);

		/* Check if the maximum output is in the index of the actual maximum output's */
		if (maxIndex(chromoOutput) != maxIndex(expectedOutput))
			threshError++;
	}

	return threshError / (getNumDataSetSamples(data));
}

double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	/* Routine check */
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

	int i;
	double threshError = 0;
	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		/* Get actual output and expected class */
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo, 0);
		int expectedClass = getDataSetSampleOutputs(data, i)[0];

		/* If the actual output doesn't lie in the threshold range that defined by the class */
		/* Error counter increments by 1 */
		if (chromoOutput < threshold + (expectedClass - 1) * threshIncre || chromoOutput >= threshold + expectedClass * threshIncre)
		{
			threshError++;
		}
	}

	return threshError / (getNumDataSetSamples(data));
}

double totalSum(struct parameters *params, struct chromosome *chromo, struct dataSet *data)
{
	/* Routine check */
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

	int i = 0;
	double error = 0;

	for (i = 0; i < getNumDataSetSamples(data); i++)
	{
		executeChromosome(chromo, getDataSetSampleInputs(data, i));
		double chromoOutput = getChromosomeOutput(chromo, 0);
		double expectedOutput = getDataSetSampleOutputs(data, i)[0];

		/* Set error threshold */
		if(fabs(expectedOutput - chromoOutput) >= 8)
            error++;
	}

	return error / getNumDataSetSamples(data);
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

int getBestEntity(void)
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
	return 1;
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
	}
	else if (strcmp(arr,"TS") == 0){
		setCustomFitnessFunction(params, totalSum, "TS");
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

