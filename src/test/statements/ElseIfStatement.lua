if a then
    a = 1
elseif b then
    a = 2
elseif f() < #tbl[a] then
    a = 3
elseif (f() < #tbl[a]) and obj:method(a, b) then
    a = 4
elseif (obj:method(a, b) ~= tbl["x"]) or (f().x < #tbl[a]) then
    a = 5
elseif ((a and b) or c) and ((f() < #tbl[a]) or (g() > tbl[b])) then
    a = 6
end
