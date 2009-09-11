<?php

require_once(sfConfig::get('sf_lib_dir').'/filter/base/BaseFormFilterPropel.class.php');

/**
 * IntentBelongstoIntentcategory filter form base class.
 *
 * @package    mashup
 * @subpackage filter
 * @author     Your name here
 * @version    SVN: $Id: sfPropelFormFilterGeneratedTemplate.php 16976 2009-04-04 12:47:44Z fabien $
 */
class BaseIntentBelongstoIntentcategoryFormFilter extends BaseFormFilterPropel
{
  public function setup()
  {
    $this->setWidgets(array(
      'intent_id'                          => new sfWidgetFormPropelChoice(array('model' => 'Intent', 'add_empty' => true)),
      'intentCategory_id'                  => new sfWidgetFormPropelChoice(array('model' => 'Intentcategory', 'add_empty' => true)),
    ));

    $this->setValidators(array(
      'intent_id'                          => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Intent', 'column' => 'id_intent')),
      'intentCategory_id'                  => new sfValidatorPropelChoice(array('required' => false, 'model' => 'Intentcategory', 'column' => 'id_intentCategory')),
    ));

    $this->widgetSchema->setNameFormat('intent_belongsto_intentcategory_filters[%s]');

    $this->errorSchema = new sfValidatorErrorSchema($this->validatorSchema);

    parent::setup();
  }

  public function getModelName()
  {
    return 'IntentBelongstoIntentcategory';
  }

  public function getFields()
  {
    return array(
      'id_intent_belongsTo_intentCategory' => 'Number',
      'intent_id'                          => 'ForeignKey',
      'intentCategory_id'                  => 'ForeignKey',
    );
  }
}
