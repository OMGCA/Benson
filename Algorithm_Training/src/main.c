#include <stdio.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include "cgp-sls.h"
#include <time.h>

double thresHold[] = {1,2,3};
double simpleThresholdClassifier(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data);
int maxIndex(double* arr);
char** importCGPParams(void);
void getBestEntity(void);

int main(void)
{

	struct parameters *params = NULL;
	struct dataSet *trainingData = NULL;
	struct dataSet *validationData = NULL;
	struct dataSet *testData = NULL;
	struct chromosome *chromo = NULL;

	char** cgp_params = importCGPParams();
	thresHold[0] = atof(cgp_params[0]);
	thresHold[1] = atof(cgp_params[1]);
	thresHold[2] = atof(cgp_params[2]);

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

	setShortcutConnections(params,0);

	if(numOutputs == 1)
		setCustomFitnessFunction(params, simpleThresholdClassifier, "STC");
	else if(numOutputs == 4)
		setCustomFitnessFunction(params, fourOutputFitnessFunction, "XT4");

	setShortcutConnections(params, 0);

	setUpdateFrequency(params, updateFrequency);

	printParameters(params);

	// Note: you may need to check this path such that it is relative to your executable
	trainingData = initialiseDataSetFromFile("./01_training.csv");
	validationData = initialiseDataSetFromFile("./02_validation.csv");
	testData = initialiseDataSetFromFile("./03_testing.csv");

	chromo = runValiTestCGP(params, trainingData, validationData, testData, numGens);

	printChromosome(chromo, 0);

	saveChromosome(chromo,"latest_chromo.chromo");

	saveChromosomeDot(chromo, 0, "chromo.dot");

	if (numOutputs == 1)
	{
		int i;
		int mismatchError = 0;
		for (i = 0; i < getNumDataSetSamples(testData); i++)
		{

			executeChromosome(chromo, getDataSetSampleInputs(testData, i));
			double chromoOutput = 0;

			chromoOutput = getChromosomeOutput(chromo, 0);
			int expectedOutput = getDataSetSampleOutputs(testData, i)[0];
			printf("%.2f ", chromoOutput);

			switch (expectedOutput)
			{
			case 1:
				if (chromoOutput > thresHold[0])
				{
					mismatchError++;
					printf("Mismatch");
				}
				else
					printf("Match");

				break;
			case 2:
				if (chromoOutput > thresHold[1] || chromoOutput <= thresHold[0])
				{
					mismatchError++;
					printf("Mismatch");
				}
				else
					printf("Match");
				break;
			case 3:
				if (chromoOutput > thresHold[2] || chromoOutput <= thresHold[1])
				{
					mismatchError++;
					printf("Mismatch");
				}
				else
					printf("Match");
				break;
			case 4:
				if (chromoOutput < thresHold[2])
				{
					mismatchError++;
					printf("Mismatch");
				}
				else
					printf("Match");
				break;
			}

			printf(" %.2f", getDataSetSampleOutputs(testData, i)[0]);
			printf("\n");
		}

		printf("Accuracy = %.4f (%d/%d)", 100 - ((float)mismatchError * 100 / getNumDataSetSamples(testData)), (getNumDataSetSamples(testData) - mismatchError), getNumDataSetSamples(testData));

		printf("\n");
	}

	else if (numOutputs == 4){
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
	getBestEntity();

	freeDataSet(trainingData);
	freeDataSet(validationData);
	freeDataSet(testData);
	freeChromosome(chromo);
	freeParameters(params);

	return 0;
}

int maxIndex(double* arr){
	int i = 0;
	int max = 0;
	double tmp = arr[0];

	for(i = 0; i < 4; i++){
		if(tmp < arr[i]){
			tmp = arr[i];
			max = i;
		}

	}

	return max;
}

double fourOutputFitnessFunction(struct parameters *params, struct chromosome *chromo, struct dataSet *data){
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

	for(i = 0; i < getNumDataSetSamples(data); i++){
		executeChromosome(chromo, getDataSetSampleInputs(data,i));
		double *chromoOutput = malloc(4*sizeof(double));
		double *expectedOutput = getDataSetSampleOutputs(data,i);


		int j = 0;
		for(j = 0; j < 4; j++){
			chromoOutput[j] = getChromosomeOutput(chromo,j);
			//threshError += pow(chromoOutput[j] - expectedOutput[j],2);
		}

		if(maxIndex(chromoOutput) != maxIndex(expectedOutput))
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
		double chromoOutput = getChromosomeOutput(chromo,0);
		int expectedClass = getDataSetSampleOutputs(data,i)[0];

		switch(expectedClass){
			case 1:
				if(chromoOutput > thresHold[0])
					threshError++;
				break;
			case 2:
				if(chromoOutput > thresHold[1] || chromoOutput <= thresHold[0])
					threshError++;
				break;
			case 3:
				if(chromoOutput > thresHold[2] || chromoOutput <= thresHold[1])
					threshError++;
				break;
			case 4:
				if(chromoOutput < thresHold[2])
					threshError++;
				break;
		}

	}

	return threshError / (getNumDataSetSamples(data));
}

char** importCGPParams(void){
    FILE* fp;
    char line[256];
    int i = 0;

    char **cgp_params = malloc(11*sizeof(char*));

    fp = fopen("cgp_params.txt","r");

    if(fp == NULL){
        printf("File not found");
        return 0;
    }

    for(i = 0; i < 11; i++){
        cgp_params[i] = malloc(10*sizeof(char));
    }
    i = 0;

    while(fgets(line, sizeof(line), fp)){
        strcpy(cgp_params[i],line);
        i++;
    }

    fclose(fp);

    return cgp_params;
}

void getBestEntity(void){
    FILE* fp;
    char line[256];
    int i = 0;

    double tmpBest[4] = {0,0,0,0};

    fp = fopen("CGP_Output.txt","r");
    if(fp == NULL){
        printf("File not found.");
        return 0;
    }

    while(fgets(line, sizeof(line), fp)){
		i = 0;
        char* pch = strtok(line," ");
		char** fitnessSeg = malloc(4*sizeof(char*));

        while(pch != NULL){
			fitnessSeg[i] = malloc(10*sizeof(char));
			strcpy(fitnessSeg[i],pch);
            pch = strtok(NULL," ");
			i++;
        }

		if(atof(fitnessSeg[1]) >= atof(fitnessSeg[2]) && atof(fitnessSeg[2]) >= atof(fitnessSeg[3])){
			if(atof(fitnessSeg[3]) > tmpBest[3]){
				for(i = 0; i < 4; i++){
					tmpBest[i] = atof(fitnessSeg[i]);
				}
			}
		}


    }
	printf("\nBest gen at %.0f with fitness of %.2f, %.2f and %.2f.\n", tmpBest[0], tmpBest[1], tmpBest[2], tmpBest[3]);

}
