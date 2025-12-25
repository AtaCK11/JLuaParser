local a
local a = 1
local a = b
local a = b + c
local a = -b
local a = not b
local a = #tbl[a]

local a = {}
local a = {1, 2}
local a = {x = 1}
local a = {[a] = b, [f()] = g()}

local a = function() end
local a = function(x) return x end
local a = function(...) return ... end

local a = f()
local a = obj:method(a, b)
local a = tbl[1]
local a = tbl[a]
local a = f().x
local a = (f() < #tbl[a]) and 1 or 2

local a, b
local a, b = 1, 2
local a, b = f(), g()
local a, b = tbl[1], tbl[2]
local a, b = obj:method(), f().x
