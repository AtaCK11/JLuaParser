if f() < #tbl[a] then
    a = tbl[1]
    tbl[a] = {}
    tbl[1], a = f(), {}
else
    a = function(...) return ... end
    tbl[a], tbl[b] = b, a
    a, b = obj:method(), f().x
end
