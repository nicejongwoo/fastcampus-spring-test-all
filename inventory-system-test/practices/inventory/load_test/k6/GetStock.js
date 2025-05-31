import http from 'k6/http';
import { sleep, check } from 'k6';

const maxVus = 1000; // Maximum number of virtual users


/**
 * ((maxVus * 5) / 2) + (maxVus * 10) + ((maxVus * 5) / 2) = 15 * maxVus
 * 15 * maxVus 초 동안 실행
 * 250ms delay이기 때문
 * 총 15 * maxVus * 4 = 60 * maxVus 번 실행
 * @type {{scenarios: {getStock: {executor: string, startVUs: number, stages: [{duration: string, target: number}]}}}}
 */
export const options = {
  // vus: 10,
  // duration: '30s',
  scenarios: {
    getStock: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: [
        {duration: "5s", target: maxVus},
        {duration: "10s", target: maxVus},
        {duration: "5s", target: 0},
      ]
    }
  }
};

export default function() {
  const res = http.get('http://localhost:8080/api/v1/inventory/1');

  // console.log(`res: ${JSON.stringify(res)}`);
  // res: {"remote_ip":"127.0.0.1","remote_port":8080,"url":"http://localhost:8080/api/v1/inventory/1","status":200,"status_text":"200 ","proto":"HTTP/1.1","headers":{"Date":"Sat, 31 May 2025 01:53:04 GMT","Content-Type":"application/json"},"cookies":{},"body":"{\"data\":{\"item_id\":\"1\",\"stock\":10000},\"error\":null}","timings":{"duration":1.586,"blocked":0.001,"looking_up":0,"connecting":0,"tls_handshaking":0,"sending":0.003,"waiting":1.572,"receiving":0.011},"tls_version":"","tls_cipher_suite":"","ocsp":{"produced_at":0,"this_update":0,"next_update":0,"revoked_at":0,"revocation_reason":"","status":""},"error":"","error_code":0,"request":{"method":"GET","url":"http://localhost:8080/api/v1/inventory/1","headers":{"User-Agent":["Grafana k6/1.0.0"]},"body":"","cookies":{}}}  source=console

  check(res, {
    "status is 200": (res) => res.status === 200,
    "should have item_id 1": (res) => res.json("data.item_id") === '1',
    "should have stock >= 0": (res) => res.json("data.stock") >= 0,
  });

  /**
   *
   */
  sleep(0.25);
}
