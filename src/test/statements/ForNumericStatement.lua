for i = 1, 10 do end
for i = 1, 10, 2 do end

for i = f(), g() do
    a = i
end

for i = f() + 1, g() * 2 do
    a = i
end

for i = -f(), #(tbl[a]) do
    tbl[i] = f(i)
end

for i = (f() < #tbl[a]) and 1 or 2, (g() > tbl[b]) and 10 or 20, 1 do
    a = i
end
