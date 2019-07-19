str = 'Benson_Data/Patients/LEEDS_p14270314_bensonrecall.txt';
rawData = load(str);
patientID = extractBetween(str,"LEEDS_","_benson");
drawingMode = extractBetween(str,"_benson",".txt");

for c = 1:length(rawData(:,1))
    rawData(c,1) = c;
end

rawX = rawData(:,2);
rawY = rawData(:,3);
rawTimeStamp = rawData(:,1);
rawPen = rawData(:,6);
sampleRate = 50;
sdSampleRate = 50;
maxSampleRate = 300;

velocity = zeros(size(rawX));
for c = sampleRate+1:size(rawX)
    velocity(c) = (((rawX(c)-rawX(c-sampleRate)).^2+(rawY(c)-rawY(c-sampleRate)).^2).^0.5)/10000;
end

velocitySD = zeros(floor(size(rawX,1)/sdSampleRate),1);
counter = 1;
for c = 1:size(velocitySD)
    velocitySD(c) = std2(velocity(counter:counter+sdSampleRate));
    counter = counter + sdSampleRate;
end

velocityMax = zeros(floor(size(rawX,1)/maxSampleRate),1);
counter = 1;
for c = 1:size(velocityMax)
    velocityMax(c) = max(velocity(counter:counter+maxSampleRate));
    counter = counter + maxSampleRate;
end


velocityOff = velocity;
for c = 1:size(rawX)
    if rawPen(c) == 0
        velocity(c) = NaN;
    end
    
    if rawPen(c) ~= 0
        velocityOff(c) = NaN;
    end
end







penSeg = 0;
for c = 1:size(rawPen)
    if(rawPen(c) ~= 0)
        if(rawPen(c+1) == 0)
            penSeg = penSeg + 1;
        end
    end
end
angleCounter = zeros(9,1);
angleCounter2 = zeros(4,1);
gradient = zeros(size(rawX));
for c = sampleRate+1:size(rawX)
    gradient(c) = (rawY(c) - rawY(c-sampleRate))/(rawX(c) - rawX(c-sampleRate));
    if(atan(gradient(c))*180/pi <= 70 && atan(gradient(c))*180/pi >= 30 && rawPen(c) ~= 0)
        if(rawX(c) >= rawX(c-sampleRate))
            angleCounter2(3,1) = angleCounter2(3,1) + 1;
        end
    end
    if(atan(gradient(c))*180/pi <= -30 && atan(gradient(c))*180/pi >= -70 && rawPen(c) ~= 0)
        if(rawX(c) <= rawX(c-sampleRate))
            angleCounter2(4,1) = angleCounter2(4,1) + 1;
        end
    end
end
angleCatagoris = [2 3 4 5];
angle = atan(gradient)*180/pi;

for c = 1:size(angle)
    if(isnan(angle(c)) == 1)
        angle(c) = 0;
    end
    for d = 1:9
        % Only pen ON is counted
        if ( abs(angle(c)) >= d*10-10 && abs(angle(c)) < d*10 && rawPen(c) ~= 0)
            angleCounter(d) = angleCounter(d) + 1;
        end
        
    end
    if ( abs(angle(c)) == 90 && rawPen(c) ~= 0)
            angleCounter(9) = angleCounter(9) + 1;
    end
end

angleCopy = angle;
angleCounter2(1,1) = angleCounter(1,1);
angleCounter2(2,1) = angleCounter(9,1);

angle = abs(angle);

angleSD = zeros(floor(size(rawX,1)/sdSampleRate),1);
counter = 1;
for c = 1:size(angleSD)
    angleSD(c) = std2(angle(counter:counter+sdSampleRate));
    counter = counter + sdSampleRate;
end






penSegIndex = 1;
penTimeStampIndex = 1;
penOnTimeStamp = [];
for c = 1:size(rawPen)
    if(rawPen(c) ~= 0)
        penOnTimeStamp(penSegIndex,penTimeStampIndex) = rawTimeStamp(c);
        penTimeStampIndex = penTimeStampIndex + 1;
        if(rawPen(c+1) == 0 && penSegIndex < penSeg)
            penSegIndex = penSegIndex + 1;
            penTimeStampIndex = 1;
        end
    end
end
segX = [];
segY = [];
for c = 1:penSeg
    for d = 1:length(penOnTimeStamp)
        if(penOnTimeStamp(c,d) ~= 0)
            segX(c,d) = rawX(penOnTimeStamp(c,d));
            segY(c,d) = rawY(penOnTimeStamp(c,d));
        end
    end
end
segX(segX==0) = NaN;
segY(segY==0) = NaN;
segCounter = zeros(penSeg,2);
for c = 1:penSeg
    for d = 1:length(segX(c,:))
        if(isnan(segX(c,d)) ~=1 && isnan(segY(c,d)) ~= 1)
            segCounter(c,1) = segCounter(c,1) + 1;
        elseif(isnan(segX(c,d)) == 1)
            segCounter(c,1) = segCounter(c,1);
        end
    end
end

gradiTmp = NaN(penSeg,length(segX));

for c = 1:penSeg
    if(isnan(gradiTmp(c,1)) == 1)
        gradiTmp(c,1) = 0;
    end
    
    for d = sampleRate+1:segCounter(c)
        if((segY(c,d) - segY(c,d-sampleRate))/(segX(c,d)-segX(c,d-sampleRate)) == Inf || (segY(c,d)-segY(c,d-sampleRate))/(segX(c,d)-segX(c,d-sampleRate)) == -Inf)
            gradiTmp(c,d-sampleRate) = 90;
        elseif(isnan((segY(c,d) - segY(c,d-sampleRate))/(segX(c,d)-segX(c,d-sampleRate))) == 1 && d ~= sampleRate+1)
            gradiTmp(c,d-sampleRate) = gradiTmp(c,d-sampleRate-1);
        elseif(isnan(gradiTmp(c,1)) == 0 || d > sampleRate+1)
            gradiTmp(c,d-sampleRate) = atan(abs((segY(c,d)-segY(c,d-sampleRate))/(segX(c,d)-segX(c,d-sampleRate))))*180/pi;
        end
    end
end


figure(1);
clf;
hold on;
for c = 1:penSeg
    plot(-segX(c,:),segY(c,:),'LineWidth',0.25*c);
end
legend("Seg1","Seg2","Seg3","Seg4","Seg5","Seg6","Seg7","Seg8","Seg9","Seg10","Seg11","Seg12","Seg13","Seg14","Seg15","Seg16","Seg17","Seg18","Seg19","Seg20","Seg21","Seg22");
xlim([0 1]);
ylim([0 1]);
axis equal;


figure(2);
clf;
hold on;
graphTitle = strcat("Angle-Time graph of ",patientID,", drawing mode ",drawingMode,", per segment");
title(graphTitle);
xlabel('Time');
ylabel('Angle (deg)');
for c = 1:penSeg
    plot(gradiTmp(c,:),'LineWidth',c*0.05);
end
legend("Seg1","Seg2","Seg3","Seg4","Seg5","Seg6","Seg7","Seg8","Seg9","Seg10","Seg11","Seg12","Seg13","Seg14","Seg15","Seg16","Seg17","Seg18","Seg19","Seg20","Seg21","Seg22");
ylim([-15 105]);
set(gca,'FontSize',12);
saveas(gcf,strcat(graphTitle,".png"));

figure(3);
clf;

subplot(2,1,1);
plot(velocity, 'LineWidth',1);
title(strcat("Velocity-Time graph of ",patientID,", drawing mode ",drawingMode));
hold on;
plot(velocityOff,'-.r','color','r');
ylim([0 5e-5]);
legend("Pen on paper","Pen off paper");
subplot(2,1,2);
plot(velocitySD,'LineWidth',1);
title(strcat("Velocity SD graph of ",patientID,", drawing mode ",drawingMode));
ylim([0 2e-5]);
% 
% 
% subplot(2,1,1);
% plot(angle,'LineWidth',1);
% graphTitle = strcat("Angle-Time graph of ",patientID,", drawing mode ",drawingMode);
% title(graphTitle);
% xlabel('Time');
% ylabel('Angle (deg)');
% ylim([-10 100]);
% set(gca,'FontSize',12);
% subplot(2,1,2);
% plot(angleSD,'LineWidth',1);
% title(strcat("Angle SD graph of ",patientID,", drawing mode ",drawingMode));
% ylim([0 40]);
% set(gca,'FontSize',12);
% saveas(gcf,strcat(graphTitle,".png"));

figure(5);
clf;
plot(velocityMax);

figure(6);
clf;
penBin = zeros(size(rawPen));
for c = 1:size(rawPen)
    if(rawPen(c,1) == 0)
        penBin(c,1) = 0;
    else
        penBin(c,1) = 1;
    end
end

plot(penBin,'.');
title(strcat("Pen-Paper interaction of ",patientID,", drawing mode ",drawingMode));
ylim([-0.5 1.5]);


