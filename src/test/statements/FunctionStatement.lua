function f()
end

function f(a)
    return a
end

function f(a, b)
    local c = a + b
    return c
end

function f(...)
    return ...
end

function tbl.f(a)
    return a
end

function tbl.x.y(a)
    return a
end
