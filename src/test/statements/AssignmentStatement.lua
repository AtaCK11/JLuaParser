a = 1
a = "s"
a = true
a = nil

a = b
a = b + c
a = (b + c) * d
a = -b
a = not b
a = #tbl

a = {}
a = {1, 2, 3}
a = {x = 1, y = 2}
a = {[a] = b, [f()] = g()}

a = function() end
a = function(x) return x end
a = function(...) return ... end

a = f()
a = f(1, 2)
a = obj:method(a, b)

a = tbl[a]
a = tbl["x"]
a = tbl[1]
a = tbl[f()]
a = tbl[a + b]
a = tbl[a].b
a = tbl[a][b]

tbl[1] = 1
tbl[a] = b
tbl[f()] = g()
tbl[a + b] = c

tbl.x = 1
tbl.x.y = 2
tbl["x"] = 3
tbl["x"].y = 4

tbl[a].b = 10
tbl[a][b] = 11
(tbl[a])[b] = 12

a, b = 1, 2
a, b = b, a
a, b = f(), g()
a, b = tbl[1], tbl[2]
a, b = tbl[a], tbl[b]
a, b = obj:method(), f().x
a, b = (f() < #tbl[a]) and 1 or 2, (g() > tbl[b]) and 3 or 4

tbl[1], a = 1, 2
tbl[a], tbl[b] = b, a
tbl.x, tbl.y = 1, 2
tbl[a].b, tbl[a][b] = f(), g()
