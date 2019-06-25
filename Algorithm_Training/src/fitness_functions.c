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

int getBestEntity(char* randomNum)
{
	FILE *fp;
	char line[256];
	int i = 0;

	double tmpBest[4] = {0, 0, 0, 0};
	char outputFileName[20] = "_CGP_Output.txt";
	strtok(randomNum, "\n");
	strcat(randomNum, outputFileName);

	fp = fopen(randomNum, "r");
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
			if (atof(fitnessSeg[2]) == tmpBest[2])
			{
				if (atof(fitnessSeg[3]) >= tmpBest[3])
				{
					for (i = 0; i < 4; i++)
					{
						tmpBest[i] = atof(fitnessSeg[i]);
					}
				}
			}
			else if(atof(fitnessSeg[2]) > tmpBest[2] && fabs(atof(fitnessSeg[2]) - atof(fitnessSeg[3])) < 30)
            {
                for (i = 0; i < 4; i++)
                {
                    tmpBest[i] = atof(fitnessSeg[i]);
                }
			}
		}
		for(i = 0; i < 4; i++){
            free(fitnessSeg[i]);
		}
		free(fitnessSeg);
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
		printf("Entity %d: \n", i+1);
		printf("CGP Output: ");
		int j = 0;
		for(j = 0; j < 4; j++){
            chromoOutput[j] = getChromosomeOutput(chromo,j);
			printf("%.2f ", chromoOutput[j]);
		}
		printf("\nSoftmax Probability: ");
		double* softmaxOutput = softmax(chromoOutput,4);
		for(j = 0; j < 4; j++){
            printf("%.2f%% ", softmaxOutput[j]*100);
		}
		printf("\nExpected Class: ");
		pdDecode(maxIndex(expectedOutput));
		printf("\nCGP Output Class: ");
		pdDecode(maxIndex(chromoOutput));
		if(maxIndex(chromoOutput) != maxIndex(expectedOutput)){
			mismatchError++;
		}
//
//		else
//			printf("Match ");

//		for(j = 0; j < 4; j++){
//			printf("%.2f ", expectedOutput[j]);
//		}

		printf("\n\n");

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

void runKFold(struct parameters *params, int numGens, int kFoldVar, char *fitnessFunction, char* randomNum){
	char *kFoldDataSrc = "./kfolddata";

	struct chromosome* kFoldChromo[kFoldVar];
	struct dataSet* kFoldTraining[kFoldVar];
	struct dataSet* kFoldValidation[kFoldVar];
	struct dataSet* kFoldTest[kFoldVar];

	int i = 0;
	for(i = 0; i < kFoldVar; i++){
        kFoldChromo[i] = NULL;
        kFoldTraining[i] = NULL;
        kFoldValidation[i] = NULL;
        kFoldTest[i] = NULL;

        char foldIndex[kFoldVar];
        char nFold[80];

        strcpy(nFold, kFoldDataSrc);
        strcat(nFold,"/fold_");
        itoa(i,foldIndex,kFoldVar);
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

        getBestEntity(randomNum);

        //printf("%s\n%s\n%s\n",foldTrain, foldValidate, foldTest);
        freeDataSet(kFoldTraining[i]);
        freeDataSet(kFoldValidation[i]);
	}

	for(i = 0; i < kFoldVar; i++){
        printf("\nIteration %d: \n", i);
        setDisplayAction(fitnessFunction, kFoldChromo[i], kFoldTest[i]);
        freeChromosome(kFoldChromo[i]);
        freeDataSet(kFoldTest[i]);
	}

}

double* softmax(double arr[], int arrLength)
{
    double logSum = 0;
    double* logArr = malloc(arrLength * sizeof(double));
    double* logAns = malloc(arrLength * sizeof(double));
    int i = 0;

    for(i = 0; i < arrLength; i++)
    {

        logArr[i] = exp(arr[i]);
        //printf("%.3f ",exp(arr[i]));

        logSum+=exp(arr[i]);
    }
    for(i = 0; i < arrLength; i++)
    {
        logAns[i] = logArr[i] / logSum;
    }

    return logAns;

}

void pdDecode(int index)
{
    if(index == 0)
        printf("PD-NC");
    else if(index == 1)
        printf("PD-MCI");
    else if(index == 2)
        printf("PD-D");
    else if(index == 3)
        printf("HC");
}

char **importFile(char *fileName, int arrSize)
{
    FILE *fp;
    char line[1024];
    int i = 0;

    char **strArr = malloc(arrSize * sizeof(char *));

    fp = fopen(fileName, "r");

    if(fp == NULL)
    {
        printf("File not found");
        return 0;
    }

    for(i = 0; i < arrSize; i++)
    {
        strArr[i] = malloc(1024 * sizeof(char));
    }
    i = 0;

    while(fgets(line, sizeof(line), fp))
    {
        strcpy(strArr[i], line);
        i++;
    }

    fclose(fp);
    return strArr;
}

char *strSplit(char *strArr, char *delimiter, int index)
{
    char *ptr = strtok(strArr, delimiter);
    if(index != 0)
    {

        int i = 0;
        for(i = 0; i < index; i++)
        {
            ptr = strtok(NULL, delimiter);
        }
    }



    return ptr;
}
