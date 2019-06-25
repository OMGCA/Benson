dataPath = 'Benson_Data\Patients\';
addpath(dataPath);
listing = dir(strcat(dataPath, '*.txt'));

for c = 1:size(listing)
    
    sampleName = listing(c).name;
    Data = load(sampleName);
    timeStamp = Data(:,1);
    xAxis = Data(:,2);
    yAxis = Data(:,3);
    
    SampleRate = 25;

    velocityList = zeros(floor(size(timeStamp)/SampleRate));
    i = 1;
    for d = 1:size(velocityList)
        distance = 0;
        for e = 1:SampleRate
            distance = distance + sqrt( (xAxis(i+1)-xAxis(i)) * (xAxis(i+1)-xAxis(i)) + (yAxis(i+1)-yAxis(i)) * (yAxis(i+1)-yAxis(i)) ) ;
             i = i+1;
             if( i >= size(xAxis))
                break;
             end
           
        end
        velocityList(d) = distance;
        if( i >= size(xAxis))
            break;
        end
    end

    plot(velocityList);
    
    % Figure configuration
     title(sampleName);
     xlabel('Time'); 
     ylabel('Distance');
    
     % resize the figure
     set(gcf, 'Position', [200 200 1200 350]);
    
     %Save file
     baseFile = strcat(erase(strcat(sampleName), ".txt"),'_distance.png');
     newFileName = strcat(dataPath,baseFile);
     saveas(gcf,newFileName);
   
end
