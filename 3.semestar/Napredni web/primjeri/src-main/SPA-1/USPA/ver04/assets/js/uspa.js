import Observer from "./lib/Observer.js";

export default class Uspa {
    async setView(viewName) {        
        let viewClass = this.currView.getViews()[viewName];
        this.currViewObject = new viewClass(); // should I cache it?
        await this.render();
        console.log("pushing", `${this.stubUrl}/${viewName}`);
        history.pushState(null, null, `${this.stubUrl}/${viewName}`);
        this.bindViewData();
    }
    bindViewData() {
        if (this.currViewObject.getData) {
            let currViewData = this.currViewObject.getData();
            this.currViewProxy = new Observer(currViewData, this.currViewObject.onChange.bind(this.currViewObject));
            document.querySelectorAll("[uspa-bind]").forEach((elem) => {
                let name = elem.getAttribute("uspa-bind");
                let proxy = this.currViewProxy.getProxy();
                elem.value = proxy[name];  // could also set via currViewData[name] but this way I can trap this via get in proxy
                elem.onkeyup = () => {
                    // console.log("before", this.currViewObject.getData());
                    proxy[name] = elem.value;  // could use some metadata and do parsing, eg int, date, etc
                    // console.log("after", this.currViewObject.getData());
                    if (this.currViewProxy.isDirty()) {
                        this.render(name);
                    }
                };
            });
        }
        
    }
    async render(focusOnElementName) {  // silly rendering...
        console.log("rendering", new Date().toLocaleTimeString());
        document.querySelector("router-view").innerHTML = await this.currViewObject.getHtml();
        this.bindViewData();
        if (focusOnElementName) {
            document.querySelector(`[uspa-bind="${focusOnElementName}"]`).focus();
        }
    }
    mount(selector, initalView) {
        this.currView = initalView;
        this.stubUrl = window.location.pathname;  // this is only because of server-side ver00 magic
        initalView.getHtml()
            .then(html => {
                document.getElementById(selector).innerHTML = html;
                return html;
            }).then(html => {
                if (html.indexOf("data-link" > 0)) {
                    document.body.addEventListener("click", e => {
                        if (e.target.matches("[data-link]")) {
                            e.preventDefault();
                            let viewName = e.target.getAttribute("href");
                            this.setView(viewName);
                        }
                    });
                }
            }
            ).catch(err => {
                console.log("USPA: error occured:", err);
            });

    }
}

