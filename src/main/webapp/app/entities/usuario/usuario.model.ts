import * as dayjs from 'dayjs';

export interface IUsuario {
  id?: number;
  nome?: string;
  cpf?: string | null;
  dataNascimento?: dayjs.Dayjs | null;
  criado?: dayjs.Dayjs | null;
}

export class Usuario implements IUsuario {
  constructor(
    public id?: number,
    public nome?: string,
    public cpf?: string | null,
    public dataNascimento?: dayjs.Dayjs | null,
    public criado?: dayjs.Dayjs | null
  ) {}
}

export function getUsuarioIdentifier(usuario: IUsuario): number | undefined {
  return usuario.id;
}
