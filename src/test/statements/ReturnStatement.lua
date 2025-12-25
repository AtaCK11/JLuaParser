
function f() return end
function f() return 1 end
function f() return a end
function f() return a + b end
function f() return -a end
function f() return not a end
function f() return #tbl[a] end

function f() return {} end
function f() return {1, 2} end
function f() return {x = 1, [a] = b} end

function f() return function() end end
function f() return function(x) return x end end
function f() return function(...) return ... end end

function f() return f() end
function f() return obj:method(a, b) end
function f() return tbl[a] end
function f() return f().x end
function f() return (f() < #tbl[a]) and 1 or 2 end

function f() return a, b end
function f() return f(), g() end
function f() return tbl[1], tbl[a], obj:method(), f().x end