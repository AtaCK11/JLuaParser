for k, v in pairs(tbl) do
    tbl[k] = v
end

for k in ipairs(tbl) do
    a = tbl[k]
end

for k, v in f() do
    a = k
    b = v
end

for k, v in obj:method(a, b) do
    tbl[k], a = v, tbl[k]
end
--[[
for k, v, w in f(), g(), h() do
    a = w
end
]]--