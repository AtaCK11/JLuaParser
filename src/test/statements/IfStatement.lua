if a then end
if not a then end
if (a) then end

if a == b then end
if a ~= b then end
if a < b then end
if a <= b then end
if a > b then end
if a >= b then end

if a + b < c * d then end
if (a + b) < (c * d) then end
if -a < -b then end
if #tbl < 10 then end

if tbl[a] then end
if tbl[a] == nil then end
if tbl[a] ~= nil then end
if tbl[a + b] < tbl[c] then end
if tbl["x"] == tbl.y then end

if f() then end
if f(1, 2, 3) then end
if f(a + b) then end
if (f()) then end

if obj:method() then end
if obj:method(a, b) then end

if f() < #tbl[a] then end
if f() <= #tbl[a] then end
if f() > #tbl[a] then end
if f() >= #tbl[a] then end
if f() == #tbl[a] then end
if f() ~= #tbl[a] then end

if f() < #(tbl[a]) then end
if (f()) < (#(tbl[a])) then end

if (f() + g()) < (#tbl[a] + 10) then end
if (f() - g()) < (#tbl[a] - 10) then end
if (f() * g()) < (#tbl[a] * 10) then end
if (f() / g()) < (#tbl[a] / 10) then end
if (f() % g()) < (#tbl[a] % 10) then end
if (f() ^ g()) < (#tbl[a] ^ 2) then end

if a and b then end
if a or b then end
if (a and b) or c then end
if a and (b or c) then end
if (not a) and b then end

if (a < b) and (c < d) then end
if (a < b) or (c < d) then end
if ((a < b) and (c < d)) or (e == f) then end

if (f() < #tbl[a]) and (g() > tbl[b]) then end
if (f() < #tbl[a]) or (g() > tbl[b]) then end

if (f() < #tbl[a]) and obj:method(a, b) then end
if obj:method(a, b) and (f() < #tbl[a]) then end

if (f() < #tbl[a]) and (obj:method(a, b) == tbl["x"]) then end
if (obj:method(a, b) ~= tbl["x"]) or (f() < #tbl[a]) then end

if ((f()).x) then end
if (f()).x == 1 then end
if (f()).x < #tbl[a] then end

if f().x then end
if f().x < #tbl[a] then end

if (f()).x.y then end
if (f()).x[y] then end
if (f()).x[y] < #tbl[a] then end

if tbl[a].b then end
if tbl[a].b < #tbl[a] then end
if tbl[a]["b"] < #tbl[a] then end
if tbl[a][b] < #tbl[a] then end

if (tbl[a])[b] < #tbl[a] then end

if (a and b) and (c and d) then end
if (a or b) or (c or d) then end

if (a and b) or (c and d) then end
if (a or b) and (c or d) then end

if (a < b) < c then end
if (a == b) == c then end

if (a and b) and (f() < #tbl[a]) then end
if (a or b) and (f() < #tbl[a]) then end

if (a and b) or (f() < #tbl[a]) then end
if (a or b) or (f() < #tbl[a]) then end

if (a and (b or c)) and ((f() < #tbl[a]) or obj:method()) then
    tbl[a] = f()
end
