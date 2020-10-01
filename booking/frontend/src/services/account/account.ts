import { fetchWrapper } from "../fetchWrapper";

type CreateAccountBody = {
  userName: string;
  password: string;
};

type CreateAccountResponse = {
  accountId: string;
  userName: string;
  password: string;
};

export async function createAccount(
  registerObject: CreateAccountBody
): Promise<CreateAccountResponse> {
  const response = await fetchWrapper.post("/accounts", registerObject);
  return response.json();
}
