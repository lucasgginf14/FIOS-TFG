-- Event ticket reservations.
-- Run this manually before starting the application with ddl-auto=validate.

alter table event_purchase
  add column if not exists state varchar(20);

update event_purchase
set state = 'RESERVED'
where state is null;

alter table event_purchase
  alter column state set not null;

alter table event_purchase
  add column if not exists cancelled_at timestamp(6) without time zone;

alter table event_purchase
  add column if not exists active boolean;

update event_purchase
set active = case when state = 'RESERVED' then true else null end;

update event_purchase
set price_paid = 0
where price_paid is null;

alter table event_purchase
  alter column price_paid set not null;

alter table event_purchase
  drop constraint if exists uk_event_purchase_user_event;

alter table event_purchase
  drop constraint if exists uk_event_purchase_active_user_event;

alter table only event_purchase
  add constraint uk_event_purchase_active_user_event unique (user_id, event_id, active);

alter table event_purchase
  drop constraint if exists event_purchase_state_check;

alter table only event_purchase
  add constraint event_purchase_state_check check (state in ('RESERVED', 'CANCELLED'));

create index if not exists idx_event_purchase_state
  on event_purchase using btree (state);
