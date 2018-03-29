/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// define __esModule on exports
/******/ 	__webpack_require__.r = function(exports) {
/******/ 		Object.defineProperty(exports, '__esModule', { value: true });
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = "./src/admin_client/index.ts");
/******/ })
/************************************************************************/
/******/ ({

/***/ "./src/admin_client/index.ts":
/*!***********************************!*\
  !*** ./src/admin_client/index.ts ***!
  \***********************************/
/*! no static exports found */
/***/ (function(module, exports) {

eval("class Statistic {\n    constructor(timestamp, value) {\n        this.timestamp = timestamp;\n        this.value = value;\n    }\n}\nfunction requestStatistics() {\n    return fetch('/api/statistics').then(res => {\n        if (!res.ok || res.status !== 200) {\n            throw 'Request failed';\n        }\n        return res.json();\n    });\n}\nfunction formatTimestamp(v) {\n    let date = new Date(v * 1000);\n    let year = date.getFullYear();\n    let month = ('0' + (date.getMonth() + 1)).substr(-2);\n    let day = ('0' + date.getDate()).substr(-2);\n    let hour = date.getHours();\n    let minutes = ('0' + date.getMinutes()).substr(-2);\n    let seconds = ('0' + date.getSeconds()).substr(-2);\n    return `${year}-${month}-${day} ${hour}:${minutes}:${seconds}`;\n}\ndocument.addEventListener('DOMContentLoaded', () => {\n    let tableContainer = document.getElementById('table-container');\n    function buildTable(statistics) {\n        let table = document.createElement('table');\n        let header = document.createElement('tr');\n        let headerTimestamp = document.createElement('th');\n        headerTimestamp.innerText = 'Date + time';\n        let headerValue = document.createElement('th');\n        headerValue.innerText = 'Value';\n        header.appendChild(headerTimestamp);\n        header.appendChild(headerValue);\n        table.appendChild(header);\n        for (let statistic of statistics) {\n            let row = document.createElement('tr');\n            let rowTimestamp = document.createElement('td');\n            rowTimestamp.innerText = formatTimestamp(statistic.timestamp);\n            let rowValue = document.createElement('td');\n            rowValue.classList.add('number');\n            rowValue.innerText = statistic.value.toString();\n            row.appendChild(rowTimestamp);\n            row.appendChild(rowValue);\n            table.appendChild(row);\n        }\n        for (let c of tableContainer.childNodes) {\n            tableContainer.removeChild(c);\n        }\n        tableContainer.appendChild(table);\n    }\n    requestStatistics().then(statistics => {\n        buildTable(statistics);\n        setInterval(() => requestStatistics().then(statistics => buildTable(statistics)), 5000);\n    });\n});\n\n\n//# sourceURL=webpack:///./src/admin_client/index.ts?");

/***/ })

/******/ });