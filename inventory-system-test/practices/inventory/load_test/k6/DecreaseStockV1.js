import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  vus: 1,
  duration: '15s',
};

const stockMap = {}

export default function() {

  const data = {
    quantity: 1,
  }

  const res = http.post('http://localhost:8080/api/v1/inventory/1/decrease', JSON.stringify(data), {
    headers: {
      "Content-Type": "application/json",
    }
  });

  check(res, {
    "status is 200": (res) => res.status === 200,
    "should shave item_id 1": (res) => res.json("data.item_id") === "1",
    "should shave stock >= 0": (res) => res.json("data.stock") >= 0,
  });

  if (stockMap[res.json("data.stock")]) {
    console.log("Duplicates found: " + res.json("data.stock"));
  } else {
    stockMap[res.json("data.stock")] = true;
  }

  sleep(0.25);
}
