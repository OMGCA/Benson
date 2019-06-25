dataPath = 'Benson_Data\Controls\';
addpath(dataPath);
listing = dir(strcat(dataPath, '*.txt'));

for c = 1:size(listing)
    sampleName = listing(c).name;
    
    % Load data
    Data = load(sampleName);
    try
        timeStamp = Data(:,1);
        xAxis = Data(:,2);
        yAxis = Data(:,3);

        gradient = zeros(size(xAxis));

        SampleRate = 30;

        for d = 1+SampleRate:size(xAxis)
            gradient(d) = (yAxis(d)-yAxis(d-SampleRate)) / (xAxis(d)-xAxis(d-SampleRate));
        end

        angle = abs(atan(gradient)*180/pi);

        plot(timeStamp,angle);
    
        % Figure configuration
        title(sampleName);
        xlabel('Time Stamp') 
        ylabel('Angle');
    
        % resize the figure
        set(gcf, 'Position', [200 200 1200 350]);
    
        %Save file
        baseFile = strcat(erase(strcat(sampleName), ".txt"),'_angle.png');
        newFileName = strcat(dataPath,baseFile);
        saveas(gcf,newFileName);
    catch
        disp('Empty data.');
        disp('Execution will continue.');
    end
    
end

