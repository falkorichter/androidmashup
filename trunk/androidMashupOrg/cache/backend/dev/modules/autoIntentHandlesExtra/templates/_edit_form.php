<?php echo form_tag('IntentHandlesExtra/save', array(
  'id'        => 'sf_admin_edit_form',
  'name'      => 'sf_admin_edit_form',
  'multipart' => true,
)) ?>

<?php echo object_input_hidden_tag($intent_handles_extra, 'getIdIntentHandlesExtra') ?>

<fieldset id="sf_fieldset_none" class="">

<div class="form-row">
  <?php echo label_for('intent_handles_extra[intent_id]', __($labels['intent_handles_extra{intent_id}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('intent_handles_extra{intent_id}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('intent_handles_extra{intent_id}')): ?>
    <?php echo form_error('intent_handles_extra{intent_id}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_select_tag($intent_handles_extra, 'getIntentId', array (
  'related_class' => 'Intent',
  'control_name' => 'intent_handles_extra[intent_id]',
  'include_blank' => true,
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

<div class="form-row">
  <?php echo label_for('intent_handles_extra[extra_id]', __($labels['intent_handles_extra{extra_id}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('intent_handles_extra{extra_id}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('intent_handles_extra{extra_id}')): ?>
    <?php echo form_error('intent_handles_extra{extra_id}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_select_tag($intent_handles_extra, 'getExtraId', array (
  'related_class' => 'Extra',
  'control_name' => 'intent_handles_extra[extra_id]',
  'include_blank' => true,
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

<div class="form-row">
  <?php echo label_for('intent_handles_extra[mandatory]', __($labels['intent_handles_extra{mandatory}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('intent_handles_extra{mandatory}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('intent_handles_extra{mandatory}')): ?>
    <?php echo form_error('intent_handles_extra{mandatory}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_input_tag($intent_handles_extra, 'getMandatory', array (
  'size' => 45,
  'control_name' => 'intent_handles_extra[mandatory]',
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

<div class="form-row">
  <?php echo label_for('intent_handles_extra[description]', __($labels['intent_handles_extra{description}']), '') ?>
  <div class="content<?php if ($sf_request->hasError('intent_handles_extra{description}')): ?> form-error<?php endif; ?>">
  <?php if ($sf_request->hasError('intent_handles_extra{description}')): ?>
    <?php echo form_error('intent_handles_extra{description}', array('class' => 'form-error-msg')) ?>
  <?php endif; ?>

  <?php $value = object_textarea_tag($intent_handles_extra, 'getDescription', array (
  'size' => '30x3',
  'control_name' => 'intent_handles_extra[description]',
)); echo $value ? $value : '&nbsp;' ?>
    </div>
</div>

</fieldset>

<?php include_partial('edit_actions', array('intent_handles_extra' => $intent_handles_extra)) ?>

</form>

<ul class="sf_admin_actions">
      <li class="float-left"><?php if ($intent_handles_extra->getIdIntentHandlesExtra()): ?>
<?php echo button_to(__('delete'), 'IntentHandlesExtra/delete?id_intent_handles_extra='.$intent_handles_extra->getIdIntentHandlesExtra(), array (
  'post' => true,
  'confirm' => __('Are you sure?'),
  'class' => 'sf_admin_action_delete',
)) ?><?php endif; ?>
</li>
  </ul>
