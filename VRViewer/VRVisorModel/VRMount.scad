union() {
	translate([-75, 10, 0])
		cube([152, 10, 70], center = true);
	translate([-7.22, 5, 28.5])
		linear_extrude(height = 5) import("hook.dxf");
	translate([-7.22, 5, -33.5])
		linear_extrude(height = 5) import("hook.dxf");
	translate([-150, -70, -10])
		linear_extrude(height = 20) import("loop.dxf");
	translate([5, 12.5, 0])
		cube([17, 5, 70], center = true);
}