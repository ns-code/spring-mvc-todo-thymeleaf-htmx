package dev.thymeleafhtmx.web;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import static org.assertj.core.api.Assertions.assertThat;

class TodosPlaywrightTest {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String TODOS_URL = BASE_URL + "/todos";
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    @BeforeAll
    static void initPlayWright() {
        // TodosDB.initTodos();
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

        context = browser.newContext(
                new Browser.NewContextOptions());
    }

    @AfterAll
    static void closePlayWright() {
        if (browser != null)
            browser.close();
        if (playwright != null)
            playwright.close();
    }

    @Test
    void todosPlaywrightWorkflow() {
        try (Page page = context.newPage()) {
            page.navigate(TODOS_URL);
            page.setDefaultNavigationTimeout(60000);
            page.setDefaultTimeout(60000);

            page.locator("#categoryFilter").selectOption("2");
            Locator goForRun = page.getByText("Go for a run");
            Locator goToGym = page.getByText("Go to the gym");
            goForRun.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            goToGym.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            assertThat(goForRun.isVisible()).isTrue();
            assertThat(goToGym.isVisible()).isTrue();

            page.locator("#new-todo").click();
            page.locator("#new-todo").fill("two");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add")).click();

            goForRun.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            goToGym.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            Locator twoTodo = page.getByText("two");
            twoTodo.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            assertThat(goForRun.isVisible()).isTrue();
            assertThat(goToGym.isVisible()).isTrue();
            assertThat(twoTodo.isVisible()).isTrue();

            twoTodo.click();
            page.locator("#edit-todo").press("End");
            page.locator("#edit-todo").fill("two updated");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save")).click();

            Locator updatedTodo = page.getByText("two updated");
            updatedTodo.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            assertThat(updatedTodo.isVisible()).isTrue();

            updatedTodo.hover();
            page.getByTitle("Delete todo").nth(2).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete")).click();

            goForRun.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            assertThat(page.getByText("two updated").isVisible()).isFalse();
            assertThat(goForRun.isVisible()).isTrue();
            assertThat(goToGym.isVisible()).isTrue();
        }
    }
}
