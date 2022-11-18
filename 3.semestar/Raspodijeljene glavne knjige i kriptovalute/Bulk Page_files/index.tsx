import { createHotContext as __vite__createHotContext } from "/bulk-page/@vite/client";import.meta.hot = __vite__createHotContext("/src/index.tsx");import RefreshRuntime from "/bulk-page/@react-refresh";
let prevRefreshReg;
let prevRefreshSig;
if (import.meta.hot) {
  if (!window.__vite_plugin_react_preamble_installed__) {
    throw new Error("@vitejs/plugin-react can't detect preamble. Something is wrong. See https://github.com/vitejs/vite-plugin-react/pull/11#discussion_r430879201");
  }
  prevRefreshReg = window.$RefreshReg$;
  prevRefreshSig = window.$RefreshSig$;
  window.$RefreshReg$ = (type, id) => {
    RefreshRuntime.register(type, "/Users/eduardduras/Desktop/strukani/risk/bulk-page/risk.bulk-page.web-ui/src/index.tsx " + id);
  };
  window.$RefreshSig$ = RefreshRuntime.createSignatureFunctionForTransform;
}
var _jsxFileName = "/Users/eduardduras/Desktop/strukani/risk/bulk-page/risk.bulk-page.web-ui/src/index.tsx", _s = $RefreshSig$();
import __vite__cjsImport2_react from "/bulk-page/node_modules/.vite/deps/react.js?v=ef0f375e"; const useCallback = __vite__cjsImport2_react["useCallback"]; const version = __vite__cjsImport2_react["version"];
import { ErrorBoundary } from "/bulk-page/node_modules/.vite/deps/@sentry_react.js?v=ef0f375e";
import __vite__cjsImport4__superbetGroup_trading_apps_webUiComponents from "/bulk-page/node_modules/.vite/deps/@superbet-group_trading_apps_web-ui-components.js?v=ef0f375e"; const Auth = __vite__cjsImport4__superbetGroup_trading_apps_webUiComponents["Auth"]; const PrivateRoute = __vite__cjsImport4__superbetGroup_trading_apps_webUiComponents["PrivateRoute"]; const PWAService = __vite__cjsImport4__superbetGroup_trading_apps_webUiComponents["PWAService"]; const openUpdateModal = __vite__cjsImport4__superbetGroup_trading_apps_webUiComponents["openUpdateModal"];
import { Button, Result } from "/bulk-page/node_modules/.vite/deps/antd.js?v=ef0f375e";
import { DndProvider } from "/bulk-page/node_modules/.vite/deps/react-dnd.js?v=ef0f375e";
import { HTML5Backend } from "/bulk-page/node_modules/.vite/deps/react-dnd-html5-backend.js?v=ef0f375e";
import __vite__cjsImport8_reactDom from "/bulk-page/node_modules/.vite/deps/react-dom.js?v=ef0f375e"; const ReactDOM = __vite__cjsImport8_reactDom.__esModule ? __vite__cjsImport8_reactDom.default : __vite__cjsImport8_reactDom;
import { Provider } from "/bulk-page/node_modules/.vite/deps/react-redux.js?v=ef0f375e";
import { BrowserRouter, Route, Switch } from "/bulk-page/node_modules/.vite/deps/react-router-dom.js?v=ef0f375e";
import { registerSW } from "/bulk-page/@vite-plugin-pwa/virtual:pwa-register";
import "/bulk-page/node_modules/@superbet-group/trading.apps.web-ui-components/lib/style.css";
import "/bulk-page/src/global.scss";
import { baseUrl, environment, sentryDsn } from "/bulk-page/src/environment.ts";
import OverrideTicketsModal from "/bulk-page/src/features/ManualOverride/components/OverrideModal.tsx?t=1668688594945";
import styles from "/bulk-page/src/index.module.scss";
import Filters from "/bulk-page/src/shared/components/Filters.tsx";
import Header from "/bulk-page/src/shared/components/Header.tsx";
import TableOptionsModal from "/bulk-page/src/shared/components/TableOptionsModal.tsx";
import TicketTable from "/bulk-page/src/shared/components/TicketTable.tsx";
import { ConfirmationModalContextProvider } from "/bulk-page/src/shared/hooks/useConfirmationModal.tsx";
import logger from "/bulk-page/src/shared/services/logger.ts";
import sentry from "/bulk-page/src/shared/services/sentry.ts";
import store from "/bulk-page/src/store.ts";
import TicketsActionsDrawer from "/bulk-page/src/features/TicketsActions/components/TicketsActionsDrawer.tsx?t=1668690602980";
import __vite__cjsImport26_react_jsxDevRuntime from "/bulk-page/node_modules/.vite/deps/react_jsx-dev-runtime.js?v=ef0f375e"; const _jsxDEV = __vite__cjsImport26_react_jsxDevRuntime["jsxDEV"];
const authPath = "/auth";
if (!["local", "dev", "qa", "stage", "production"].includes(environment)) {
  throw new Error(`Unknown environment value ${environment}, expected one of local, dev, qa, stage or production.`);
}
if (environment !== "local") {
  const debug = environment !== "production";
  sentry.initialize(environment, debug, sentryDsn, version);
}
logger.initialize(environment === "local");
const ErrorFallback = () => {
  _s();
  const onReloadClick = useCallback(() => {
    window.location.reload();
  }, []);
  return /* @__PURE__ */ _jsxDEV("div", {
    className: styles["error-fallback"],
    children: /* @__PURE__ */ _jsxDEV(Result, {
      status: "error",
      title: "Ooops",
      subTitle: "An unexpected error occurred",
      extra: /* @__PURE__ */ _jsxDEV(Button, {
        onClick: onReloadClick,
        children: "Reload Page"
      }, void 0, false, {
        fileName: _jsxFileName,
        lineNumber: 61,
        columnNumber: 24
      }, void 0)
    }, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 57,
      columnNumber: 13
    }, void 0)
  }, void 0, false, {
    fileName: _jsxFileName,
    lineNumber: 56,
    columnNumber: 9
  }, void 0);
};
_s(ErrorFallback, "n/64nD16yP8ZUXqdcsV2b63C/dM=");
_c = ErrorFallback;
const App = () => /* @__PURE__ */ _jsxDEV(ConfirmationModalContextProvider, {
  children: /* @__PURE__ */ _jsxDEV("div", {
    className: styles.app,
    children: [/* @__PURE__ */ _jsxDEV(Header, {}, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 70,
      columnNumber: 13
    }, void 0), /* @__PURE__ */ _jsxDEV(Filters, {}, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 71,
      columnNumber: 13
    }, void 0), /* @__PURE__ */ _jsxDEV(TicketTable, {}, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 72,
      columnNumber: 13
    }, void 0), /* @__PURE__ */ _jsxDEV(TicketsActionsDrawer, {}, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 73,
      columnNumber: 13
    }, void 0), /* @__PURE__ */ _jsxDEV(TableOptionsModal, {}, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 74,
      columnNumber: 13
    }, void 0), /* @__PURE__ */ _jsxDEV(OverrideTicketsModal, {}, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 75,
      columnNumber: 13
    }, void 0)]
  }, void 0, true, {
    fileName: _jsxFileName,
    lineNumber: 69,
    columnNumber: 9
  }, void 0)
}, void 0, false, {
  fileName: _jsxFileName,
  lineNumber: 68,
  columnNumber: 5
}, void 0);
_c2 = App;
const Application = /* @__PURE__ */ _jsxDEV(ErrorBoundary, {
  fallback: /* @__PURE__ */ _jsxDEV(ErrorFallback, {}, void 0, false, {
    fileName: _jsxFileName,
    lineNumber: 81,
    columnNumber: 30
  }, void 0),
  children: /* @__PURE__ */ _jsxDEV(Provider, {
    store,
    children: /* @__PURE__ */ _jsxDEV(BrowserRouter, {
      basename: baseUrl,
      children: /* @__PURE__ */ _jsxDEV(DndProvider, {
        backend: HTML5Backend,
        children: /* @__PURE__ */ _jsxDEV(Switch, {
          children: [/* @__PURE__ */ _jsxDEV(Route, {
            path: authPath,
            component: Auth
          }, void 0, false, {
            fileName: _jsxFileName,
            lineNumber: 86,
            columnNumber: 25
          }, void 0), /* @__PURE__ */ _jsxDEV(PrivateRoute, {
            authPath,
            component: App
          }, void 0, false, {
            fileName: _jsxFileName,
            lineNumber: 87,
            columnNumber: 25
          }, void 0)]
        }, void 0, true, {
          fileName: _jsxFileName,
          lineNumber: 85,
          columnNumber: 21
        }, void 0)
      }, void 0, false, {
        fileName: _jsxFileName,
        lineNumber: 84,
        columnNumber: 17
      }, void 0)
    }, void 0, false, {
      fileName: _jsxFileName,
      lineNumber: 83,
      columnNumber: 13
    }, void 0)
  }, void 0, false, {
    fileName: _jsxFileName,
    lineNumber: 82,
    columnNumber: 9
  }, void 0)
}, void 0, false, {
  fileName: _jsxFileName,
  lineNumber: 81,
  columnNumber: 5
}, void 0);
ReactDOM.render(Application, document.getElementById("root"));
const updateSW = registerSW({
  onNeedRefresh() {
    openUpdateModal(() => updateSW(true));
  }
});
const SW_CHECK_INTERVAL_MINUTES = 5;
PWAService.initializeCheckInterval(SW_CHECK_INTERVAL_MINUTES);
var _c, _c2;
$RefreshReg$(_c, "ErrorFallback");
$RefreshReg$(_c2, "App");
if (import.meta.hot) {
  window.$RefreshReg$ = prevRefreshReg;
  window.$RefreshSig$ = prevRefreshSig;
  import.meta.hot.accept();
  if (!window.__vite_plugin_react_timeout) {
    window.__vite_plugin_react_timeout = setTimeout(() => {
      window.__vite_plugin_react_timeout = 0;
      RefreshRuntime.performReactRefresh();
    }, 30);
  }
}

//# sourceMappingURL=data:application/json;base64,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QUFmO0FBQUEsRUFDbEI7QUFIdUIsQ0FBRDtBQU0zQixNQUFNSSw0QkFBNEI7QUFDbENDLFdBQVdDLHdCQUF3QkYseUJBQW5DIiwibmFtZXMiOlsiYXV0aFBhdGgiLCJpbmNsdWRlcyIsImVudmlyb25tZW50IiwiRXJyb3IiLCJkZWJ1ZyIsInNlbnRyeSIsImluaXRpYWxpemUiLCJzZW50cnlEc24iLCJ2ZXJzaW9uIiwibG9nZ2VyIiwiRXJyb3JGYWxsYmFjayIsIm9uUmVsb2FkQ2xpY2siLCJ1c2VDYWxsYmFjayIsIndpbmRvdyIsImxvY2F0aW9uIiwicmVsb2FkIiwic3R5bGVzIiwiQXBwIiwiYXBwIiwiQXBwbGljYXRpb24iLCJiYXNlVXJsIiwiSFRNTDVCYWNrZW5kIiwiQXV0aCIsIlJlYWN0RE9NIiwicmVuZGVyIiwiZG9jdW1lbnQiLCJnZXRFbGVtZW50QnlJZCIsInVwZGF0ZVNXIiwicmVnaXN0ZXJTVyIsIm9uTmVlZFJlZnJlc2giLCJvcGVuVXBkYXRlTW9kYWwiLCJTV19DSEVDS19JTlRFUlZBTF9NSU5VVEVTIiwiUFdBU2VydmljZSIsImluaXRpYWxpemVDaGVja0ludGVydmFsIl0sInNvdXJjZXMiOlsiL1VzZXJzL2VkdWFyZGR1cmFzL0Rlc2t0b3Avc3RydWthbmkvcmlzay9idWxrLXBhZ2Uvcmlzay5idWxrLXBhZ2Uud2ViLXVpL3NyYy9pbmRleC50c3giXSwiZmlsZSI6Ii9Vc2Vycy9lZHVhcmRkdXJhcy9EZXNrdG9wL3N0cnVrYW5pL3Jpc2svYnVsay1wYWdlL3Jpc2suYnVsay1wYWdlLndlYi11aS9zcmMvaW5kZXgudHN4Iiwic291cmNlc0NvbnRlbnQiOlsiaW1wb3J0IHsgdXNlQ2FsbGJhY2ssIHZlcnNpb24gfSBmcm9tIFwicmVhY3RcIjtcblxuaW1wb3J0IHsgRXJyb3JCb3VuZGFyeSB9IGZyb20gXCJAc2VudHJ5L3JlYWN0XCI7XG5pbXBvcnQge1xuICAgIEF1dGgsXG4gICAgUHJpdmF0ZVJvdXRlLFxuICAgIFBXQVNlcnZpY2UsXG4gICAgb3BlblVwZGF0ZU1vZGFsLFxufSBmcm9tIFwiQHN1cGVyYmV0LWdyb3VwL3RyYWRpbmcuYXBwcy53ZWItdWktY29tcG9uZW50c1wiO1xuaW1wb3J0IHsgQnV0dG9uLCBSZXN1bHQgfSBmcm9tIFwiYW50ZFwiO1xuaW1wb3J0IHsgRG5kUHJvdmlkZXIgfSBmcm9tIFwicmVhY3QtZG5kXCI7XG5pbXBvcnQgeyBIVE1MNUJhY2tlbmQgfSBmcm9tIFwicmVhY3QtZG5kLWh0bWw1LWJhY2tlbmRcIjtcbmltcG9ydCBSZWFjdERPTSBmcm9tIFwicmVhY3QtZG9tXCI7XG5pbXBvcnQgeyBQcm92aWRlciB9IGZyb20gXCJyZWFjdC1yZWR1eFwiO1xuaW1wb3J0IHsgQnJvd3NlclJvdXRlciwgUm91dGUsIFN3aXRjaCB9IGZyb20gXCJyZWFjdC1yb3V0ZXItZG9tXCI7XG4vLyBlc2xpbnQtZGlzYWJsZS1uZXh0LWxpbmUgaW1wb3J0L25vLXVucmVzb2x2ZWRcbmltcG9ydCB7IHJlZ2lzdGVyU1cgfSBmcm9tIFwidmlydHVhbDpwd2EtcmVnaXN0ZXJcIjtcblxuaW1wb3J0IFwiQHN1cGVyYmV0LWdyb3VwL3RyYWRpbmcuYXBwcy53ZWItdWktY29tcG9uZW50cy9saWIvc3R5bGUuY3NzXCI7XG5pbXBvcnQgXCIuL2dsb2JhbC5zY3NzXCI7XG5cbmltcG9ydCB7IGJhc2VVcmwsIGVudmlyb25tZW50LCBzZW50cnlEc24gfSBmcm9tIFwiLi9lbnZpcm9ubWVudFwiO1xuaW1wb3J0IE92ZXJyaWRlVGlja2V0c01vZGFsIGZyb20gXCIuL2ZlYXR1cmVzL01hbnVhbE92ZXJyaWRlL2NvbXBvbmVudHMvT3ZlcnJpZGVNb2RhbFwiO1xuaW1wb3J0IHN0eWxlcyBmcm9tIFwiLi9pbmRleC5tb2R1bGUuc2Nzc1wiO1xuaW1wb3J0IEZpbHRlcnMgZnJvbSBcIi4vc2hhcmVkL2NvbXBvbmVudHMvRmlsdGVyc1wiO1xuaW1wb3J0IEhlYWRlciBmcm9tIFwiLi9zaGFyZWQvY29tcG9uZW50cy9IZWFkZXJcIjtcbmltcG9ydCBUYWJsZU9wdGlvbnNNb2RhbCBmcm9tIFwiLi9zaGFyZWQvY29tcG9uZW50cy9UYWJsZU9wdGlvbnNNb2RhbFwiO1xuaW1wb3J0IFRpY2tldFRhYmxlIGZyb20gXCIuL3NoYXJlZC9jb21wb25lbnRzL1RpY2tldFRhYmxlXCI7XG5pbXBvcnQgeyBDb25maXJtYXRpb25Nb2RhbENvbnRleHRQcm92aWRlciB9IGZyb20gXCIuL3NoYXJlZC9ob29rcy91c2VDb25maXJtYXRpb25Nb2RhbFwiO1xuaW1wb3J0IGxvZ2dlciBmcm9tIFwiLi9zaGFyZWQvc2VydmljZXMvbG9nZ2VyXCI7XG5pbXBvcnQgc2VudHJ5IGZyb20gXCIuL3NoYXJlZC9zZXJ2aWNlcy9zZW50cnlcIjtcbmltcG9ydCBzdG9yZSBmcm9tIFwiLi9zdG9yZVwiO1xuaW1wb3J0IFRpY2tldHNBY3Rpb25zRHJhd2VyIGZyb20gXCIuL2ZlYXR1cmVzL1RpY2tldHNBY3Rpb25zL2NvbXBvbmVudHMvVGlja2V0c0FjdGlvbnNEcmF3ZXJcIjtcblxuY29uc3QgYXV0aFBhdGggPSBcIi9hdXRoXCI7XG5cbmlmICghW1wibG9jYWxcIiwgXCJkZXZcIiwgXCJxYVwiLCBcInN0YWdlXCIsIFwicHJvZHVjdGlvblwiXS5pbmNsdWRlcyhlbnZpcm9ubWVudCkpIHtcbiAgICB0aHJvdyBuZXcgRXJyb3IoXG4gICAgICAgIGBVbmtub3duIGVudmlyb25tZW50IHZhbHVlICR7ZW52aXJvbm1lbnR9LCBleHBlY3RlZCBvbmUgb2YgbG9jYWwsIGRldiwgcWEsIHN0YWdlIG9yIHByb2R1Y3Rpb24uYFxuICAgICk7XG59XG5cbmlmIChlbnZpcm9ubWVudCAhPT0gXCJsb2NhbFwiKSB7XG4gICAgY29uc3QgZGVidWcgPSBlbnZpcm9ubWVudCAhPT0gXCJwcm9kdWN0aW9uXCI7XG4gICAgc2VudHJ5LmluaXRpYWxpemUoZW52aXJvbm1lbnQsIGRlYnVnLCBzZW50cnlEc24sIHZlcnNpb24pO1xufVxuXG5sb2dnZXIuaW5pdGlhbGl6ZShlbnZpcm9ubWVudCA9PT0gXCJsb2NhbFwiKTtcblxuY29uc3QgRXJyb3JGYWxsYmFjayA9ICgpID0+IHtcbiAgICBjb25zdCBvblJlbG9hZENsaWNrID0gdXNlQ2FsbGJhY2soKCkgPT4ge1xuICAgICAgICB3aW5kb3cubG9jYXRpb24ucmVsb2FkKCk7XG4gICAgfSwgW10pO1xuXG4gICAgcmV0dXJuIChcbiAgICAgICAgPGRpdiBjbGFzc05hbWU9e3N0eWxlc1tcImVycm9yLWZhbGxiYWNrXCJdfT5cbiAgICAgICAgICAgIDxSZXN1bHRcbiAgICAgICAgICAgICAgICBzdGF0dXM9XCJlcnJvclwiXG4gICAgICAgICAgICAgICAgdGl0bGU9XCJPb29wc1wiXG4gICAgICAgICAgICAgICAgc3ViVGl0bGU9XCJBbiB1bmV4cGVjdGVkIGVycm9yIG9jY3VycmVkXCJcbiAgICAgICAgICAgICAgICBleHRyYT17PEJ1dHRvbiBvbkNsaWNrPXtvblJlbG9hZENsaWNrfT5SZWxvYWQgUGFnZTwvQnV0dG9uPn1cbiAgICAgICAgICAgIC8+XG4gICAgICAgIDwvZGl2PlxuICAgICk7XG59O1xuXG5jb25zdCBBcHAgPSAoKSA9PiAoXG4gICAgPENvbmZpcm1hdGlvbk1vZGFsQ29udGV4dFByb3ZpZGVyPlxuICAgICAgICA8ZGl2IGNsYXNzTmFtZT17c3R5bGVzLmFwcH0+XG4gICAgICAgICAgICA8SGVhZGVyIC8+XG4gICAgICAgICAgICA8RmlsdGVycyAvPlxuICAgICAgICAgICAgPFRpY2tldFRhYmxlIC8+XG4gICAgICAgICAgICA8VGlja2V0c0FjdGlvbnNEcmF3ZXIgLz5cbiAgICAgICAgICAgIDxUYWJsZU9wdGlvbnNNb2RhbCAvPlxuICAgICAgICAgICAgPE92ZXJyaWRlVGlja2V0c01vZGFsIC8+XG4gICAgICAgIDwvZGl2PlxuICAgIDwvQ29uZmlybWF0aW9uTW9kYWxDb250ZXh0UHJvdmlkZXI+XG4pO1xuXG5jb25zdCBBcHBsaWNhdGlvbiA9IChcbiAgICA8RXJyb3JCb3VuZGFyeSBmYWxsYmFjaz17PEVycm9yRmFsbGJhY2sgLz59PlxuICAgICAgICA8UHJvdmlkZXIgc3RvcmU9e3N0b3JlfT5cbiAgICAgICAgICAgIDxCcm93c2VyUm91dGVyIGJhc2VuYW1lPXtiYXNlVXJsfT5cbiAgICAgICAgICAgICAgICA8RG5kUHJvdmlkZXIgYmFja2VuZD17SFRNTDVCYWNrZW5kfT5cbiAgICAgICAgICAgICAgICAgICAgPFN3aXRjaD5cbiAgICAgICAgICAgICAgICAgICAgICAgIDxSb3V0ZSBwYXRoPXthdXRoUGF0aH0gY29tcG9uZW50PXtBdXRofSAvPlxuICAgICAgICAgICAgICAgICAgICAgICAgPFByaXZhdGVSb3V0ZSBhdXRoUGF0aD17YXV0aFBhdGh9IGNvbXBvbmVudD17QXBwfSAvPlxuICAgICAgICAgICAgICAgICAgICA8L1N3aXRjaD5cbiAgICAgICAgICAgICAgICA8L0RuZFByb3ZpZGVyPlxuICAgICAgICAgICAgPC9Ccm93c2VyUm91dGVyPlxuICAgICAgICA8L1Byb3ZpZGVyPlxuICAgIDwvRXJyb3JCb3VuZGFyeT5cbik7XG5cblJlYWN0RE9NLnJlbmRlcihBcHBsaWNhdGlvbiwgZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoXCJyb290XCIpKTtcblxuY29uc3QgdXBkYXRlU1cgPSByZWdpc3RlclNXKHtcbiAgICBvbk5lZWRSZWZyZXNoKCkge1xuICAgICAgICBvcGVuVXBkYXRlTW9kYWwoKCkgPT4gdXBkYXRlU1codHJ1ZSkpO1xuICAgIH0sXG59KTtcblxuY29uc3QgU1dfQ0hFQ0tfSU5URVJWQUxfTUlOVVRFUyA9IDU7XG5QV0FTZXJ2aWNlLmluaXRpYWxpemVDaGVja0ludGVydmFsKFNXX0NIRUNLX0lOVEVSVkFMX01JTlVURVMpO1xuIl19