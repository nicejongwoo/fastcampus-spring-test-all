import http from 'k6/http';
import { sleep, check } from 'k6';

const maxVus = 1500

// maxVus * 5 / 2 + maxVus * 10 + maxVus * 5 / 2 = 15 * maxVusAdd commentMore actions
// 15 * maxVus 초 동안 실행
// 250ms delay이기 때문에
// 총 15 * maxVus * 4 = 60 * maxVus 번 실행
export const options = {
  scenarios: {
    decreaseStock: {
      executor: "ramping-vus",
      startVUs: 0,
      stages:[
        {duration: "5s", target: maxVus},
        {duration: "10s", target: maxVus},
        {duration: "5s", target: 0},
      ],
    }
  }
};

const stockMap = {}

export default function() {

  const data = {
    quantity: 1,
  }

  const res = http.post('http://localhost:8080/api/v1/inventory/1/decrease', JSON.stringify(data), {
    headers: {
      "Content-Type": "application/json",
    },
    timeout: "1s",
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

/**
 *
 *   █ TOTAL RESULTS
 *
 *     checks_total.......................: 104447 5160.657692/s
 *     checks_succeeded...................: 99.96% 104409 out of 104447
 *     checks_failed......................: 0.03%  38 out of 104447
 *
 *     ✗ status is 200
 *       ↳  99% — ✓ 34803 / ✗ 19
 *     ✗ should shave item_id 1
 *       ↳  99% — ✓ 34803 / ✗ 19
 */

/**
 *   █ TOTAL RESULTS (개선 후)
 *
 *     checks_total.......................: 113757  5619.485309/s
 *     checks_succeeded...................: 100.00% 113757 out of 113757
 *     checks_failed......................: 0.00%   0 out of 113757
 *
 *     ✓ status is 200
 *     ✓ should shave item_id 1
 *     ✓ should shave stock >= 0
 */