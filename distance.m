function [d] = distance(m)
    d = (m(1:end - 1,1) - m(2:end,1)).^2;
    d = d + (m(1:end - 1,2) - m(2:end,2)).^2;
    d = d + (m(1:end - 1,3) - m(2:end,3)).^2;
    sqrt(d);
end

