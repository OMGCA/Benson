patientData = load("patient_plot.csv");
controlData = load("control_plot.csv");

patientVelcSD = sort(patientData(:,5));
patientAngleSD = sort(patientData(:,6));

patientLenSizeRatio = zeros(110,1);
controlLenSizeRatio = zeros(53,1);
for c = 1:109
    patientLenSizeRatio(c,1) = patientData(c,4)/patientData(c,3);
end

for c = 1:53
    controlLenSizeRatio(c,1) = controlData(c,3)/controlData(c,2);
end

patientLenSizeRatio = sort(patientLenSizeRatio);
controlLenSizeRatio = sort(controlLenSizeRatio);

controlVelcSD = sort(controlData(:,4));
controlAngleSD = sort(controlData(:,5));

