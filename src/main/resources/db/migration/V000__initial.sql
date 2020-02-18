create table tg_mass_send_job
(
    id serial PRIMARY KEY,
    create_ts timestamp not null default current_timestamp,
    txt text not null
);

create table tg_mass_msg(
    id serial PRIMARY KEY,
    create_ts timestamp not null default current_timestamp,
    update_ts timestamp not null default current_timestamp,
    version   int not null default 0,
    tg_mass_send_job_id bigint not null references tg_mass_send_job,
    user_id bigint not null,
    status text not null,
    unique(tg_mass_send_job_id, user_id)
);

create index on tg_mass_msg(status, update_ts);

create table tg_mass_msg_error(
    id serial PRIMARY KEY,
    create_ts timestamp not null default current_timestamp,
    tg_mass_msg_id bigint not null references tg_mass_msg,
    try_num int not null,
    http_status int not null,
    response text not null
);


