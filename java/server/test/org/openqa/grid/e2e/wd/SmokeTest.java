package org.openqa.grid.e2e.wd;

import org.openqa.grid.common.GridRole;
import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.e2e.utils.GridTestHelper;
import org.openqa.grid.e2e.utils.RegistryTestHelper;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.grid.web.Hub;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

@Test(groups = {"slow", "firefox"})
public class SmokeTest {

  private Hub hub;

  @BeforeClass(alwaysRun = false)
  public void prepare() throws Exception {

    hub = GridTestHelper.getHub();


    SelfRegisteringRemote remote =
        GridTestHelper.getRemoteWithoutCapabilities(hub.getUrl(), GridRole.WEBDRIVER);
    remote.addBrowser(DesiredCapabilities.firefox(), 1);

    remote.startRemoteServer();

    remote.getConfiguration().put(RegistrationRequest.TIME_OUT, -1);
    remote.sendRegistrationRequest();
    RegistryTestHelper.waitForNode(hub.getRegistry(), 1);
  }

  @Test
  public void test() throws MalformedURLException, InterruptedException {
    WebDriver driver = null;
    try {
      DesiredCapabilities ff = DesiredCapabilities.firefox();
      driver = new RemoteWebDriver(new URL(hub.getUrl() + "/wd/hub"), ff);
      driver.get(hub.getUrl() + "/grid/console");
      Assert.assertEquals(driver.getTitle(), "Grid overview");
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }

  @AfterClass(alwaysRun = false)
  public void stop() throws Exception {
    hub.stop();
  }
}
