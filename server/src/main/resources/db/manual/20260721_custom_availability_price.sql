-- Add pricing for custom musical-space availability.
-- Run this manually before starting the application with ddl-auto=validate.

alter table space_availability_exception
  add column if not exists price numeric(10, 2);

-- Existing CUSTOM_AVAILABILITY rows must be assigned a real positive price
-- before enabling the check constraint below. Example:
-- update space_availability_exception
-- set price = 24.00
-- where exception_type = 'CUSTOM_AVAILABILITY'
--   and price is null;

do $$
begin
  if exists (
    select 1
    from space_availability_exception
    where exception_type = 'CUSTOM_AVAILABILITY'
      and (price is null or price <= 0)
  ) then
    raise exception 'Set a positive price for every CUSTOM_AVAILABILITY row before enabling validation';
  end if;
end $$;

alter table space_availability_exception
  drop constraint if exists ck_space_availability_exception_price;

alter table space_availability_exception
  add constraint ck_space_availability_exception_price
  check (
    (exception_type = 'CUSTOM_AVAILABILITY' and price is not null and price > 0)
    or (exception_type <> 'CUSTOM_AVAILABILITY' and price is null)
  );

alter table schedule
  drop constraint if exists ck_schedule_price_positive;

alter table schedule
  add constraint ck_schedule_price_positive
  check (price > 0);
