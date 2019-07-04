str = '0_1234_CGP_Output.txt';
rawData = load(str);

uniLineWidth = 2;

gen = rawData(:,1);
training = rawData(:,2);
validation = rawData(:,3);
testing = rawData(:,4);
confidence = rawData(:,5);

figure(1);
clf;
plot(gen,training,'LineWidth',uniLineWidth);
hold on;
plot(gen, validation,'LineWidth',uniLineWidth);
plot(gen, testing,'LineWidth',uniLineWidth);
scatter(gen, confidence,100,'x');
legend("Training","Validation","Testing","Confidence");