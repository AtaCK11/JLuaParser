while a do end
while not a do end
while f() do end
while obj:method() do end

while f() < #tbl[a] do
    a = a + 1
end

while (f() < #tbl[a]) and (g() > tbl[b]) do
    tbl[a] = f()
end

while ((a and b) or c) and ((f().x < #tbl[a]) or obj:method(a, b)) do
    tbl[a], a = a, tbl[a]
end

while (a < b) < c do
    a = a + 1
end
