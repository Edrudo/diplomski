export default class Uspa {
    async setView(viewName) {        
        let viewClass = this.currView.getViews()[viewName];
        document.querySelector("router-view").innerHTML = await (new viewClass()).getHtml();
        history.pushState(null, null, `${this.stubUrl}/${viewName}`);
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

