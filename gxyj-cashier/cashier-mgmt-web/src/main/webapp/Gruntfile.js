/**
 "grunt": "^0.4.5", // 构建工具grunt
 "grunt-contrib-clean": "~0.4.0", // 文件清理
 "grunt-contrib-copy": "^0.7.0", // 文件复制
 "grunt-contrib-cssmin": "^0.11.0", // css压缩插件
 "grunt-contrib-htmlmin": "^0.4.0", // html压缩插件
 "grunt-contrib-imagemin": "^0.9.3", // 图片压缩插件
 "grunt-contrib-less": "^1.0.1", // less编译插件
 "grunt-contrib-qunit": "^0.5.2", // 单元测试插件
 "grunt-contrib-uglify": "~0.2.0", // 文件压缩插件
*/

module.exports = function(grunt) {

    //为了目录层次的灵活
    //取消原先在concat任务下手工配置文件的方式，改为动态获取html文件和view配置文件的data-main属性，再设置到concat任务中
    var path = require("path"),
        fs = require("fs"),

        _ = grunt.util._,

        cwd = path.dirname(__filename),
        mainModules = [];

    //获取html的文件的主模块配置
    parseMainModules(cwd + path.sep + "apps");

    //去重
    mainModules = _.chain(mainModules).unique().value();

    //递归获取所有文件中的主模块配置
    function parseMainModules(filePath) {
        var stat, file,
            extName, gMatched, isXmlFile, isHtmlFile,
            rHtmlStr = "<script[\\s\\S]+data-main=([\'\"]([\\S]+)[\"\'])[\\s\\S]*>[\\s]*<\/script>",
            rDataMainStr = "data-main=[\'\"]([\\S]+)[\"\']";

        if(!fs.existsSync(filePath)) {
            return;
        }

        stat = fs.statSync(filePath);

        if(stat.isDirectory(filePath)) {  //文件夹递归处理
            file = fs.readdirSync(filePath);

            for(var i in file) {
                arguments.callee(filePath + path.sep + file[i]);
            }
        }

        if(stat.isFile(filePath)) {
            extName = path.extname(filePath);
            isXmlFile = ".xml" === extName;
            isHtmlFile = ".html" === extName;

            //只处理view配置文件和html文件
            if(!isXmlFile && !isHtmlFile) {
                return;
            }

            file = fs.readFileSync(filePath, {encoding: "utf-8"});

            gMatched = file.match(isHtmlFile ? new RegExp(rHtmlStr, "mg") : new RegExp(rDataMainStr, "mg"));

            //没有查找到data-main配置的文件不处理
            if(!gMatched) {
                return;
            }

            _.chain(gMatched).forEach(function(str) {
                var sMatched = (new RegExp(rDataMainStr, "mg")).exec(str),
                    mainStr;

                if(sMatched && sMatched[1]) {
                    mainStr = sMatched[1];  //取得data-main属性值
                    if(isHtmlFile && "." === mainStr.charAt(0)) {  //html文件需要将相对路径转换成基于apps目录的完整路径
                        mainStr = path.join(path.dirname(path.relative(cwd, filePath)), mainStr);
                    }

                    if(!path.extname(mainStr)) { //补全扩展名
                        mainStr += ".js";
                    }
                    mainModules.push(mainStr.replace(/[\/\\]/g, "/"));
                }
            });
        }
    }

    //qunit库路径过滤器
    //qunit库没有按照CMD规范封装也不需要转换和打包
    function isNotQunitPath(path) {
        return path.indexOf("qunit") === -1;
    };

    //初始化grunt
    grunt.initConfig({
        pkg: grunt.file.readJSON("package.json"),
        // 单元测试任务配置
        qunit: {
            options: {
                httpBase: "http://localhost:8080"
            },
            target: {
                src: ["tests/**/**.html"]
            }
        },
        // 清理目录任务配置
        clean: {
            dist: {
                src: ["dist/**/*"]
            },
            docs: {
                src: ["docs/**/*"]
            },
            appsTpls: {
                src: ["apps/tpls/src/*"]
            },
            uiTpls: {
                src: ["lib/bootstrap/tpls/src/*"]
            }
        },
        // 复制文件任务配置
        copy: {
            core: {
                files: [{
                    src: ["config.js", "loader.js", "lib/**", "apps/**", "!lib/**/*-import.css"],
                    dest: "dist/"
                }]
            },

            other: {
                files: [{
                    src: ["WEB-INF/**"],
                    dest: "dist/"
                }]
            }
        },

        cssmin: {
            options: {
                beautify: {
                    ascii_only: true
                }
            },
            target: {
                expand: true,
                src: ["lib/**/*.css", "apps/**/*.css", "!lib/**/*-import.css"],
                dest: "dist",
                filter: isNotQunitPath // 因qunit组件没有遵循CMD规范，不进行压缩
            }
        },
        // 图片压缩任务配置
        imagemin: {
            target: {
                files: [{
                    expand: true,
                    cwd: 'dist/',
                    src: ['**/*.{png,jpg,gif,bmp}'],
                    dest: 'dist/'
                }]
            }
        },

        transport: {
            options: {
                debug: false,
                paths: ["."]
            },
            target: {
                files: [{
                    expand: true,
                    cwd: "dist",
                    src: ["lib/**/*.js", "apps/**/*.js", "!apps/tpls/helper.js", "!lib/bootstrap/tpls/helper.js"],
                    dest: "dist",
                    filter: isNotQunitPath
                }]
            }
        },
        // 文本替换任务配置
        replace: {
            target: {
                src: ["dist/**/*.html"],
                overwrite: true,
                replacements: [{
                    from: /data-(main|css)=['"]([^"']+)["']/mg,
                    to: function(filePath) {
                        var path = require("path"),
                            match,
                            fileDir = path.dirname(path.relative("dist", filePath));

                        if (arguments.length > 1 && (match = arguments[3]) && match[2]) {
                            if (match[2].charAt(0) !== ".") {
                                return match[0];
                            }
                            return "data-" + match[1] + "=\"" + path.join(fileDir, match[2]).replace(/\\/g, "/") + "\"";
                        }
                    }
                }]
            },
            use: {
                src: ["dist/**/*.js"],
                overwrite: true,
                replacements: [{
                    from: /\buse\(['"]([^"']+)["']/mg,
                    to: function(filePath) {
                        var path = require("path"),
                            match,
                            fileDir = path.dirname(path.relative("dist", filePath)),
                            newSrc;
                        if (arguments.length > 1 && (match = arguments[3]) && match[1]) {
                            if (match[1].charAt(0) !== ".") {
                                return match[0];
                            } else {
                                match[1] = match[1].replace("./", "../");
                                newSrc = "use(\"" + path.join(fileDir, match[1]).replace(/\\/g, "/") + "\"";
                                return newSrc;
                            }
                        }
                    }
                }]
            },
            bootstrap:{
                src: ["lib/bootstrap/css/bootstrap.less"],
                overwrite: true,
                replacements: [{
                    from: /@bui:\.bui;/mg,
                    to: function() {
                        return "@bui:;";
                    }
                }]
            },
            bootstrapbui:{
                src: ["lib/bootstrap/css/bootstrap.less"],
                overwrite: true,
                replacements: [{
                    from: /@bui:;/mg,
                    to: function() {
                        return "@bui:.bui;";
                    }
                }]
            }
        },

        concat: {
            options: {
                paths: ["dist"],
                include: "all"
            },
            lib: {
                files: [{
                    expand: true,
                    cwd: "dist",
                    src: ["lib/bootstrap.js", "lib/core.js", "lib/lib.js"],
                    dest: "dist/",
                    ext: ".js",
                    filter: isNotQunitPath
                }]
            },
            apps: {
                expand: true,
                cwd: "dist",
                src: mainModules,
                dest: "dist/",
                ext: ".js",
                filter: isNotQunitPath
            }
        },

        uglify: {
            target: {
                files: [{
                    expand: true,
                    cwd: "dist",
                    src: ["**/*.js", "**/**/*.js", "**/**/**/*.js"],
                    dest: "dist",
                    filter: isNotQunitPath,
                    ext: ".js"
                }]
            }
        },

        htmlmin: {
            options: {
                removeComments: true,
                removeCommentsFromCDATA: true,
                collapseWhitespace: true,
                collapseBooleanAttributes: true,
                removeAttributeQuotes: false,
                removeRedundantAttributes: true,
                useShortDoctype: true,
                removeEmptyAttributes: true,
                removeOptionalTags: true
            },
            html: {
                files: [{
                    expand: true,
                    src: ["apps/**/*.html", "!apps/tpls/**/*.html"],
                    cwd: 'dist',
                    dest: "dist"
                }]
            }
        },

        filerev: {
            options: {
                encoding: "utf8",
                algorithm: "md5",
                length: "6"
            },
            assets: {
                files: [{
                    src: [
                        "dist/loader.js",
                        "dist/lib/*.js",
                        "dist/lib/i18n/*.js",
                        "dist/lib/**/*.css",
                        "dist/lib/**/*.{jpg,jpeg,gif,png}",
                        "dist/apps/**/*.js",
                        "dist/apps/**/*.css",
                        "dist/apps/**/*.{jpg,jpeg,gif,png,ico}",
                        "!dist/**/editor/**/*.{jpg,jpeg,gif,png}"
                    ]
                }]
            }
        },

        usemin: {
            html: "dist/apps/**/*.html",
            css: "dist/**/*.css",
            js: ["dist/apps/**/*.js", "dist/loader.*.js", "dist/lib/*.js", "dist/lib/i18n/*.js"],
            xml: [],
            options: {
                assetsDirs: [],
                patterns: {
                    js: [
                        [/(..\/*\/images\/[\/\w-]+\.(png|gif|bmp|jpg|jpeg|ico))/gm, 'replace image in js'],
                        [/(\bimages\/[\/\w-]+\.(png|gif|bmp|jpg|jpeg|ico))/gm, 'replace image in js'],
                        [/(\bapps\/[\/\w-]+\.(png|gif|bmp|jpg|jpeg|ico))/gm, 'replace image in js'],
                        [/\"\bapps\/([^\"]*)\"/gm, "Update the JS to reference our revved script files",
                            function(m) {
                                return m.match(/\.js$/) ? m : m + ".js";
                            },
                            function(m) {
                                return m.replace(".js", "");
                            }
                        ],
                        [/(\"\blib+([^"']+)["'])/gm, "Update the JS to reference our revved script files",
                            function(m) {
                                m = "dist/" + m.replace(/(\")/gm, "");
                                return m.match(/\.js$/) ? m : m + ".js";
                            },
                            function(m) {
                                m = "\"" + m.replace("dist/", "") + "\"";
                                return m.replace(".js", "");
                            }
                        ],
                        [/(\blib\/kui\/themes\/\w*.css)/gm, "Update the JS to reference our new css filenames"],
                        [/(\blib\/bootstrap\/css\/[\w-]*.css)/gm, "Update the JS to reference our new css filenames"]
                    ],
                    /* xml 的script 命名修改*/
                    xml: [
                        [/(\bdata-main=['"]([^"']+)["'])/gm, "Update the XML to reference our revved script files",
                            function(m) {
                                var src = m.match(/['"]([^"']+)["']/gm)[0].replace(/["']/gm,"");
                                return src.match(/\.js$/) ? src : src + ".js";
                            },
                            function(m) {
                                m = m.replace(".js","");
                                return "data-main=\""+m+"\"";
                            }
                        ],
                        [/(\bdata-css=['"]([^"']+)["'])/gm, "Update the XML to reference our revved css files",
                            function(m) {
                                var src = m.match(/['"]([^"']+)["']/gm)[0].replace(/["']/gm,"");
                                return src;
                            },
                            function(m) {
                                return "data-css=\""+m+"\"";
                            }
                        ]
                    ],
                    html: [
                        [/(\bcss\/ui-frame-common-\w*.css)/gm,"Update the HTML with the new css filenames"],
                        [/(\blib\/bui\/themes\/\w*.css)/gm,"Update the HTML with the new css filenames"]
                    ]
                }
            }
        },
        /* html模板编译 */
        tmod: {
            options: {
                minify: false,
                type: "cmd",
                cache: false
            },

            apps: {
                src: ["apps/tpls/**/*.html"],
                dest: "apps/tpls/src/template.js",
                options: {
                    base: 'apps/tpls',
                    helpers: "apps/tpls/helper.js"
                }
            },

            ui: {
                src: ["lib/bootstrap/tpls/**/*.html"],
                dest: "lib/bootstrap/tpls/src/template.js",
                options: {
                    base: 'lib/bootstrap/tpls',
                    helpers: "lib/bootstrap/tpls/helper.js"
                }
            }
        },
        watch: {
            apps: {
                files: ["<%=tmod.apps.src%>"],
                tasks: ['tmod:apps'],
                options: {
                    spawn: false
                }
            },
            ui: {
                files: ["<%=tmod.ui.src%>"],
                tasks: ['tmod:ui'],
                options: {
                    spawn: false
                }
            }
        },

        /**
         * 生成文档
        */
        jsdoc: {
            dist: {
                src: ["lib/core/*.js", "lib/lib.js", "lib/core.js", "lib/bootstrap/*.js", "lib/kui/*.js", "README.md"],
                options: {
                    destination: 'docs',
                    configure: "package.json"
                }
            }
        },
        /**
         * 编译bootstrap.css
         */
        less: {
            bootstrapbui: {
                files: {
                    "lib/bootstrap/css/bootstrap-kif.css":"lib/bootstrap/css/bootstrap.less"
                }
            },
            bootstrap: {
                files: {
                    "lib/bootstrap/css/bootstrap.css":"lib/bootstrap/css/bootstrap.less"
                }
            }
        }
    });

    /**
     * 注册npm任务
    */
    grunt.loadNpmTasks("grunt-filerev");
    grunt.loadNpmTasks("grunt-cmd-concat");
    grunt.loadNpmTasks("grunt-cmd-transport");
    grunt.loadNpmTasks("grunt-text-replace");
    grunt.loadNpmTasks("grunt-contrib-clean");
    grunt.loadNpmTasks("grunt-contrib-copy");
    grunt.loadNpmTasks("grunt-contrib-cssmin");
    grunt.loadNpmTasks("grunt-css-import");
    grunt.loadNpmTasks("grunt-contrib-imagemin");
    grunt.loadNpmTasks("grunt-contrib-uglify");
    grunt.loadNpmTasks("grunt-contrib-qunit");
    grunt.loadNpmTasks('grunt-contrib-htmlmin');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks("grunt-usemin");
    grunt.loadNpmTasks('grunt-tmod');
    grunt.loadNpmTasks('grunt-jsdoc');
    grunt.loadNpmTasks('grunt-contrib-less');

    /**
     * 默认构建任务
     */
    grunt.registerTask("default", [
        "clean:dist",
        "copy:core",
        "cssmin",
        "imagemin",
        "transport",
        "replace",
        "concat",
        "uglify",
        "filerev",
        "copy:other",
        "htmlmin",
        "usemin"
    ]);

    /**
     * 编译apps目录下的模版
     */
    /*grunt.registerTask("appsTemplate", [
        "clean:appsTpls",
        "tmod:apps",
        "watch:apps"
    ]);*/

    /**
     * 单元测试
     */
    grunt.registerTask("test", [
        "qunit"
    ]);

    /**
     * 清除dist目录
     */
    grunt.registerTask("cleandist", [
        "clean:dist"
    ]);



    //------以下任务业务开发不需要使用-------//
    /**
     * 合并bui主题样式
     */
    grunt.registerTask("buiThemes", [
        "css_import"
    ]);

    /**
     * 编译lib目录下的模版
     */
    /*grunt.registerTask("uiTemplate", [
        "clean:uiTpls",
        "tmod:ui",
        "watch:ui"
    ]);*/

    /**
     * 生成文档
     */
    grunt.registerTask("docs", [
        "clean:docs",
        "jsdoc"
    ]);

    /**
     * 编译bootstrap.less
     */
    grunt.registerTask("bootstrapless", [
        "replace:bootstrap",
        "less:bootstrap",
        "replace:bootstrapbui",
        "less:bootstrapbui"
    ]);
};