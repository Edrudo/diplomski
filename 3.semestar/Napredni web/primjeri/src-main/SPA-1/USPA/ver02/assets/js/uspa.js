
let template = `
<h1>My cookbook in the making ver2 (from the module)...</h1>
Pick one:
    <ul>
        <li><a href="http://fer.unizg.hr">Home</a></li>
        <li><a data-link href="http://fer.unizg.hr">Abroad</a></li>
    </ul>
`;

document.addEventListener("DOMContentLoaded", () => {
    document.body.addEventListener("click", e => {
        if (e.target.matches("[data-link]")) {
            e.preventDefault();
            document.getElementById("uspa-app").innerHTML = "Nemoj sine nikud ići...";
        }
    });
    document.getElementById("uspa-app").innerHTML = template;
});