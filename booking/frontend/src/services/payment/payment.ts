import { fetchWrapper } from "../fetchWrapper";

type PayBody = {
  accountId: string;
  totalAmount: number;
};

export async function pay(body: PayBody) {
  return await fetchWrapper.post("/payments", body);
}

export type GetPaymentsResponse = Array<{
  accountId: string;
  paymentId: string;
  paymentStatus: "PROCESSING";
  totalAmount: number;
}>;
export async function getPayments(): Promise<GetPaymentsResponse> {
  const result = await fetchWrapper.get("/payments");
  return await result.json();
}
