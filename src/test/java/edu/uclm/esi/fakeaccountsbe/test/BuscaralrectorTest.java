package edu.uclm.esi.fakeaccountsbe.test;



import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.*;
//import org.junit.jupiter.api.Test;

public class BuscaralrectorTest {
  private WebDriver driver;
  private Map<String, Object> vars;
  JavascriptExecutor js;



  @BeforeEach
  public void setUp() {
	  System.setProperty("webdriver.chrome.driver", "C:\\Users\\josel\\Universidad\\AÑO 5\\CUATRIMESTRE 1\\Tecnologías y Sistemas Web\\LABORATORIO\\chrome-win64\\chrome.exe");
	
	  
    driver = new ChromeDriver();
    js = (JavascriptExecutor) driver;
    vars = new HashMap<String, Object>();
  }

  @AfterEach
  public void tearDown() {
    driver.quit();
  }


  @Test
  public void testBuscarAlRector() {
    driver.get("https://directorio.uclm.es/");
    driver.manage().window().setSize(new Dimension(782, 823));
    driver.findElement(By.id("CPH_CajaCentro_tb_busqueda")).click();
    driver.findElement(By.id("CPH_CajaCentro_tb_busqueda")).sendKeys("JULIÁN GARDE");
    driver.findElement(By.id("CPH_CajaCentro_lkbtn_consultar")).click();
    driver.findElement(By.cssSelector(".fa-user")).click();
    assertEquals(driver.findElement(By.id("CPH_CajaCentro_rpt_cargos_lb_cargo_0")).getText(), "RECTOR/A");
  }

}
