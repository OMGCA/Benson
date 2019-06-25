function f = tierCount(pdCounts,pdAttri,pdAttriTier)
    pdAttriTierC = zeros(3,3);
    for c = 1:3
        for d = 1:pdCounts(c)
            if(pdAttri(d,c) <= pdAttriTier(1))
                pdAttriTierC(c,1) = pdAttriTierC(c,1) + 1;
            end
            if(pdAttri(d,c) < pdAttriTier(2) && pdAttri(d,c) > pdAttriTier(1))
                pdAttriTierC(c,2) = pdAttriTierC(c,2) + 1;
            end
            if(pdAttri(d,c) >= pdAttriTier(2))
                pdAttriTierC(c,3) = pdAttriTierC(c,3) + 1;
            end
        end
        for e = 1:3
            pdAttriTierC(c,e) = pdAttriTierC(c,e) * 100 / pdCounts(c);
        end
    end
    
    f = pdAttriTierC;
end