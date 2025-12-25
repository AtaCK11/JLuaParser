repeat
until a

repeat
    a = a + 1
until a > 10

repeat
    a = f()
until not a

repeat
    tbl[a] = f()
until f() < #tbl[a]

repeat
    a, b = f(), g()
until ((f() < #tbl[a]) and (g() > tbl[b])) or (obj:method(a, b) ~= tbl["x"])
