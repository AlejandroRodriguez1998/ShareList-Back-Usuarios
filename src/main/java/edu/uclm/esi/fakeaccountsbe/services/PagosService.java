package edu.uclm.esi.fakeaccountsbe.services;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Service
public class PagosService {
	
	static {
		Stripe.apiKey = "sk_test_51Q7a3VAnK2DDJlIhpYs919mK1ogEAqqEGqhKiDvk9DizoIy15ioqPvHwXzvGip0MmJ0sUr4Boh8gKey18FBs1nr400c229Mv0A";
	}

	public String prepararTransaccion(long importe) {
		PaymentIntentCreateParams params = new PaymentIntentCreateParams.Builder()
		.setCurrency("eur")
		.setAmount(importe)
		.build();
		
		PaymentIntent intent;
		try {
			intent = PaymentIntent.create(params);
			JSONObject jso = new JSONObject(intent.toJson());
			String clientSecret = jso.getString("client_secret");
			return clientSecret;
		} catch (StripeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

		}
		

	}
	
	
}
