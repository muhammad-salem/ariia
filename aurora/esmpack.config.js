const config = {
	outDir: '../web-server/src/main/resources/static/aurora/web_modules/',
	src: {
		files: [],
		include: ["./dist/**/*.js"],
		exclude: []
	},
	resources: {
		files: [],
		include: ["./src/**/*.*"],
		exclude: ["./src/**/*.{js,ts,tsx}"]
	},
	pathMap: { 'src': 'dist' },
	moduleResolution: "relative",
	plugins: [
		'css',
		'html',
		'json',
		'txt',
		'image',
		'audio'
	]
};
export default config;
