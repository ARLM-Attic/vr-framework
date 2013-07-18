	translate([1, 10, 1])
		cube([5, 5, 5], center = true);
	translate([1, 1, 1])
		linear_extrude(height = 20) import("triangle.dxf");