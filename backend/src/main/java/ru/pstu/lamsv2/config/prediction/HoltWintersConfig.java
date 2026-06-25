package ru.pstu.lamsv2.config.prediction;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
    Конфиг для коэффициентов метода прогнозирования Хольта-Уинтерса
*/
@Configuration
@ConfigurationProperties(prefix = "holtwinters")
public class HoltWintersConfig
{

    private volatile double alpha = 0.3;
    private volatile double beta = 0.1;
    private volatile double gamma = 0.1;

    public double getAlpha() {
        return this.alpha;
    }

    public double getBeta() {
        return this.beta;
    }

    public double getGamma() {
        return this.gamma;
    }

    public void setAlpha(double alpha)
    {
        validateCoefficient(alpha, "alpha");
        this.alpha = alpha;
    }

    public void setBeta(double beta)
    {
        validateCoefficient(beta, "beta");
        this.beta = beta;
    }

    public void setGamma(double gamma)
    {
        validateCoefficient(gamma, "gamma");
        this.gamma = gamma;
    }

    //Проверка коэффициента - должен быть от 0 до 1.
    private void validateCoefficient(double value, String name)
    {
        if (value < 0 || value > 1)
        {
            throw new IllegalArgumentException(name + " must be between 0 and 1");
        }
    }
}
